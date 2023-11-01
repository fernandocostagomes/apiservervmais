package fernandocostagomes.schemas

import kotlinx.coroutines.*
import kotlinx.serialization.Serializable
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Statement

@Serializable
data class Role(
    val roleId: Int = 0,
    val roleName: String,
    val roleDescription: String,
    val roleDate: String)
class ServiceRole(private val connection: Connection): SchemaInterface {
    companion object {
        private const val TABLE = "v_role"
        private const val COLUMN_ID = "v_role_id"
        private const val COLUMN_NAME = "v_role_name"
        private const val COLUMN_DESCRIPTION = "v_role_description"
        private const val COLUMN_DATE = "v_role_date"

        private const val COLUMN_ID_QUERY = "$COLUMN_ID SERIAL PRIMARY KEY, "
        private const val COLUMN_NAME_QUERY = "$COLUMN_NAME VARCHAR(20), "
        private const val COLUMN_DESCRIPTION_QUERY = "$COLUMN_DESCRIPTION VARCHAR(30),"
        private const val COLUMN_DATE_QUERY = "$COLUMN_DATE VARCHAR(20)"

        val listColumns = listOf(
            COLUMN_ID,
            COLUMN_NAME,
            COLUMN_DESCRIPTION,
            COLUMN_DATE
        )

        val listColumnsQuery = listOf(
            COLUMN_ID_QUERY,
            COLUMN_NAME_QUERY,
            COLUMN_DESCRIPTION_QUERY,
            COLUMN_DATE_QUERY
        )
    }

    init {
        try {
            connection.createStatement().executeUpdate(SchemaUtils.createTable( TABLE, listColumnsQuery))
        } catch (e: SQLException) {
            println(e.toString())
        }
    }

    private fun getResultSet(pResultSet: ResultSet): Role {
        return Role(
            pResultSet.getInt( COLUMN_ID ),
            pResultSet.getString( COLUMN_NAME ),
            pResultSet.getString( COLUMN_DESCRIPTION ),
            pResultSet.getString( COLUMN_DATE )
        )
    }

    private fun getPreparedStatement(pPreparedStatement: PreparedStatement, pObj: Any): PreparedStatement{
        pObj as Role
        pPreparedStatement.setString(1, pObj.roleName)
        pPreparedStatement.setString(2, pObj.roleDescription)
        pPreparedStatement.setString(3, SchemaUtils.getCurrentDate())
        return pPreparedStatement
    }

    // Create new role
    override suspend fun create( obj: Any ): Int = withContext(Dispatchers.IO) {

        val statement = connection.prepareStatement(
            SchemaUtils.insertQuery(
                TABLE,
                listColumns
            ), Statement.RETURN_GENERATED_KEYS)

        obj as Role

        val statementPos: PreparedStatement = getPreparedStatement( statement, obj )
        statementPos.executeUpdate()

        val generatedKeys = statementPos.generatedKeys

        if (generatedKeys.next()) {
            return@withContext generatedKeys.getInt(1)
        } else {
            throw Exception(SchemaUtils.UNABLE_NEW_ID_INSERTED)
        }
    }

    // Read a role
    override suspend fun read(id: Int): Role = withContext(Dispatchers.IO) {

        val statement = connection.prepareStatement( SchemaUtils.selectQuery(TABLE, COLUMN_ID, listColumns))
        statement.setInt(1, id)

        val resultSet = statement.executeQuery()

        if (resultSet.next()) {
            return@withContext getResultSet( resultSet )
        } else {
            throw Exception(SchemaUtils.RECORD_NOT_FOUND)
        }
    }

    // Update a role
    override suspend fun update( id: Int, obj: Any ) = withContext(Dispatchers.IO) {

        val statement = connection.prepareStatement( SchemaUtils.updateQuery(
            TABLE,
            listColumns,
            COLUMN_ID
        ) )

        obj as Role

        val statementPos = getPreparedStatement(statement, obj)
        statementPos.setInt(0, id)
        statementPos.executeUpdate()
    }

    // Delete a role
    override suspend fun delete(id: Int) = withContext(Dispatchers.IO) {
        val deleteRole = "DELETE FROM $TABLE WHERE $COLUMN_ID = ?;"
        val statement = connection.prepareStatement( deleteRole )
        statement.setInt(1, id)
        statement.executeUpdate()
    }

    // List all roles
    override suspend fun list(): List<Role> = withContext(Dispatchers.IO) {

        val statement = connection.prepareStatement( "SELECT * FROM $TABLE" )

        val resultSet = statement.executeQuery()

        val roleList = mutableListOf<Role>()

        while (resultSet.next()) {
            roleList.add( getResultSet( resultSet ) )
        }

        if (roleList.isNotEmpty()) {
            return@withContext roleList
        } else {
            throw Exception( SchemaUtils.RECORD_NOT_FOUND )
        }
    }
}