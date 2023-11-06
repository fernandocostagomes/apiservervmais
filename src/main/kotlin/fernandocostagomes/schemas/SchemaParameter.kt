package fernandocostagomes.schemas

import kotlinx.coroutines.*
import kotlinx.serialization.Serializable
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Statement

@Serializable
data class Parameter(
    val parameterId: Int = 0,
    val parameterCode: String,
    var parameterName: String,
    var parameterValue: String,
    var parameterDate: String)
class ServiceParameter(private val connection: Connection): SchemaInterface {
    companion object {
        private const val TABLE = "v_parameter"
        private const val COLUMN_ID = "v_parameter_id"
        private const val COLUMN_CODE = "v_parameter_code"
        private const val COLUMN_NAME = "v_parameter_name"
        private const val COLUMN_VALUE = "v_parameter_value"
        private const val COLUMN_DATA = "v_parameter_data"

        private const val COLUMN_ID_QUERY = "$COLUMN_ID SERIAL PRIMARY KEY, "
        private const val COLUMN_CODE_QUERY = "$COLUMN_CODE VARCHAR(10), "
        private const val COLUMN_NAME_QUERY = "$COLUMN_NAME VARCHAR(20), "
        private const val COLUMN_VALUE_QUERY = "$COLUMN_VALUE VARCHAR(20),"
        private const val COLUMN_DATA_QUERY = "$COLUMN_DATA VARCHAR(20) NOT NULL"

        val listColumnsQuery = listOf(
            COLUMN_ID_QUERY,
            COLUMN_CODE_QUERY,
            COLUMN_NAME_QUERY,
            COLUMN_VALUE_QUERY,
            COLUMN_DATA_QUERY
        )

        val listColumns = listOf(
            COLUMN_ID,
            COLUMN_CODE,
            COLUMN_NAME,
            COLUMN_VALUE,
            COLUMN_DATA
        )
    }

    init {
        try {
            val statement = connection.createStatement()
            statement.executeUpdate(
                SchemaUtils.createTable(
                    TABLE,
                    listColumnsQuery
                )
            )
        } catch (e: SQLException) {
            println(e.toString())
        }
    }

    private fun getResultSet(pResultSet: ResultSet): Parameter {
        return Parameter(
            pResultSet.getInt( COLUMN_ID ),
            pResultSet.getString( COLUMN_CODE ),
            pResultSet.getString( COLUMN_NAME ),
            pResultSet.getString( COLUMN_VALUE ),
            pResultSet.getString( COLUMN_DATA )
        )
    }

    private fun getPreparedStatement(pStatement: PreparedStatement, pObj: Any): PreparedStatement {
        pObj as Parameter
        pStatement.setString(1, pObj.parameterCode)
        pStatement.setString(2, pObj.parameterName)
        pStatement.setString(3, pObj.parameterValue)
        pStatement.setString(4, SchemaUtils.getCurrentDate())
        return pStatement
    }

    // Create new parameter
    override suspend fun create( obj: Any ): Int = withContext(Dispatchers.IO) {

        val statement = connection.prepareStatement(
            SchemaUtils.insertQuery(
                TABLE,
                listColumns
            ),
            Statement.RETURN_GENERATED_KEYS)

        obj as Parameter

        val statementPos: PreparedStatement = getPreparedStatement( statement, obj )
        statementPos.executeUpdate()

        val generatedKeys = statementPos.generatedKeys

        if (generatedKeys.next()) {
            return@withContext generatedKeys.getInt(1)
        } else {
            throw Exception(SchemaUtils.UNABLE_NEW_ID_INSERTED)
        }
    }

    // Read a parameter
    override suspend fun read(id: Int): Parameter = withContext(Dispatchers.IO) {

        val statement = connection.prepareStatement(
            SchemaUtils.selectQuery(
                TABLE,
                COLUMN_ID,
                listColumns
            )
        )

        statement.setInt(1, id)

        val resultSet = statement.executeQuery()

        if (resultSet.next()) {
            return@withContext getResultSet( resultSet )
        } else {
            throw Exception(SchemaUtils.RECORD_NOT_FOUND)
        }
    }

    // Update a parameter
    override suspend fun update( id: Int, obj: Any ) = withContext(Dispatchers.IO) {

        val statement = connection.prepareStatement(
            SchemaUtils.updateQuery(
                TABLE,
                listColumns,
                COLUMN_ID
            )
        )

        obj as Parameter

        val statementPos = getPreparedStatement( statement, obj )
        statementPos.setInt(0, id)
        statementPos.executeUpdate()
    }

    // Delete a parameter
    override suspend fun delete(id: Int) = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement( "DELETE FROM $TABLE WHERE $COLUMN_ID = ?;" )
        statement.setInt(1, id)
        statement.executeUpdate()
    }

    // List all parameters
    override suspend fun list(): List<Parameter> = withContext(Dispatchers.IO) {

        val statement = connection.prepareStatement(
            "SELECT * FROM $TABLE;"
        )
        val resultSet = statement.executeQuery()

        val parameterList = mutableListOf<Parameter>()

        while (resultSet.next()) {
            parameterList.add( getResultSet( resultSet ) )
        }

        if (parameterList.isNotEmpty()) {
            return@withContext parameterList
        } else {
            throw Exception(SchemaUtils.RECORD_NOT_FOUND)
        }
    }
}