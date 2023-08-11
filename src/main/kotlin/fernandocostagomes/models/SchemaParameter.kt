package fernandocostagomes.models

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
        private const val TABLE = "parameter"
        private const val COLUMN_ID = "id_parameter"
        private const val COLUMN_CODE = "code_parameter"
        private const val COLUMN_NAME = "name_parameter"
        private const val COLUMN_VALUE = "value_parameter"

        private const val CREATE_TABLE_PARAMETER =
                "CREATE TABLE IF NOT EXISTS " +
                        "$TABLE (" +
                        "$COLUMN_ID SERIAL PRIMARY KEY, " +
                        "$COLUMN_CODE INTEGER NOT NULL, " +
                        "$COLUMN_NAME VARCHAR(20), " +
                        "$COLUMN_VALUE VARCHAR(20));"

        private const val SELECT_PARAMETER_BY_ID = "SELECT " +
                "$COLUMN_CODE, " +
                "$COLUMN_NAME, " +
                "$COLUMN_VALUE FROM $TABLE WHERE $COLUMN_ID = ?;"

        private const val INSERT_PARAMETER = "INSERT INTO $TABLE (" +
                "$COLUMN_CODE, " +
                "$COLUMN_NAME, " +
                "$COLUMN_VALUE) VALUES (?, ?, ?);"

        private const val UPDATE_PARAMETER = "UPDATE $TABLE SET " +
                "$COLUMN_CODE = ?," +
                "$COLUMN_NAME = ?, " +
                "$COLUMN_VALUE = ? " +
                "WHERE $COLUMN_ID = ?;"

        private const val DELETE_PARAMETER = "DELETE FROM $TABLE WHERE $COLUMN_ID = ?;"

        private const val LIST_PARAMETER = "SELECT * FROM $TABLE}"
    }

    init {
        try {
            val statement = connection.createStatement()
            statement.executeUpdate(CREATE_TABLE_PARAMETER)
        } catch (e: SQLException) {
            println(e.toString())
        }
    }

    // Create new parameter
    override suspend fun create( obj: Any ): Int = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(INSERT_PARAMETER, Statement.RETURN_GENERATED_KEYS)
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
        val statement = connection.prepareStatement(SELECT_PARAMETER_BY_ID)
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
        val statement = connection.prepareStatement(UPDATE_PARAMETER)
        obj as Parameter
        statement.setInt(0, id)
        statement.setInt(1, obj.codeParameter)
        statement.setString(2, obj.nameParameter)
        statement.setString(3, obj.valueParameter)
        statement.executeUpdate()
    }

    // Delete a parameter
    override suspend fun delete(id: Int) = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(DELETE_PARAMETER)
        statement.setInt(1, id)
        statement.executeUpdate()
    }

    // List all parameters
    override suspend fun list(): List<Parameter> = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement( LIST_PARAMETER )
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