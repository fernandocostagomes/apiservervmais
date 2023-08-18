package fernandocostagomes.schemas

import kotlinx.coroutines.*
import kotlinx.serialization.Serializable
import java.sql.Connection
import java.sql.SQLException
import java.sql.Statement

@Serializable
data class Parameter(
    val codeParameter: Int,
    val nameParameter: String,
    val valueParameter: String)
class ServiceParameter(private val connection: Connection): SchemaInterface {
    companion object {
        private const val TABLE = "v_parameter"
        private const val COLUMN_ID = "v_parameter_id"
        private const val COLUMN_CODE = "v_parameter_code"
        private const val COLUMN_NAME = "v_parameter_name"
        private const val COLUMN_VALUE = "v_parameter_value"

        private const val COLUMN_ID_QUERY = "$COLUMN_ID SERIAL PRIMARY KEY, "
        private const val COLUMN_CODE_QUERY = "$COLUMN_CODE INTEGER NOT NULL, "
        private const val COLUMN_NAME_QUERY = "$COLUMN_NAME VARCHAR(20), "
        private const val COLUMN_VALUE_QUERY = "$COLUMN_VALUE VARCHAR(20)"

        val listColumnsQuery = listOf(
            COLUMN_ID_QUERY,
            COLUMN_CODE_QUERY,
            COLUMN_NAME_QUERY,
            COLUMN_VALUE_QUERY
        )

        val listColumns = listOf(
            COLUMN_ID,
            COLUMN_CODE,
            COLUMN_NAME,
            COLUMN_VALUE
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

    // Create new parameter
    override suspend fun create( obj: Any ): Int = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(
            SchemaUtils.insertQuery(
                TABLE,
                listColumnsQuery
            ),
            Statement.RETURN_GENERATED_KEYS)
        obj as Parameter
        statement.setInt(1, obj.codeParameter)
        statement.setString(2, obj.nameParameter)
        statement.setString(3, obj.valueParameter)
        statement.executeUpdate()

        val generatedKeys = statement.generatedKeys
        if (generatedKeys.next()) {
            return@withContext generatedKeys.getInt(1)
        } else {
            throw Exception("Unable to retrieve the id of the newly inserted parameter")
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
            val code = resultSet.getInt("code_parameter")
            val name = resultSet.getString("name_parameter")
            val value = resultSet.getString("value_parameter")
            return@withContext Parameter(code, name, value)
        } else {
            throw Exception("Record not found")
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
        statement.setInt(0, id)
        statement.setInt(1, obj.codeParameter)
        statement.setString(2, obj.nameParameter)
        statement.setString(3, obj.valueParameter)
        statement.executeUpdate()
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

            val codeParameter = resultSet.getInt( COLUMN_CODE )
            val nameParameter = resultSet.getString( COLUMN_NAME )
            val valueParameter = resultSet.getString( COLUMN_VALUE )

            val parameter = Parameter(codeParameter, nameParameter, valueParameter)
            parameterList.add( parameter )
        }

        if (parameterList.isNotEmpty()) {
            return@withContext parameterList
        } else {
            throw Exception("No records found")
        }
    }
}