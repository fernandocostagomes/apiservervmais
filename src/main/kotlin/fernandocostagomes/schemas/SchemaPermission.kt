package fernandocostagomes.schemas

import fernandocostagomes.schemas.SchemaUtils.Companion.getCurrentDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Statement

@Serializable
data class Permission(
    val permissionId: Int = 0,
    val permissionName: String,
    val permissionDescription: String,
    val permissionDate: String,
    val permission_roleId: Int,
    val permission_actionId: Int)
class ServicePermission(private val connection: Connection): SchemaInterface {
    companion object {
        private const val TABLE = "v_permission"
        private const val COLUMN_ID = "v_permission_id"
        private const val COLUMN_NAME = "v_permission_name"
        private const val COLUMN_DESCRIPTION = "v_permission_description"
        private const val COLUMN_DATE = "v_permission_date"
        private const val COLUMN_ROLE = "v_role_id"
        private const val COLUMN_ACTION = "v_action_id"

        private const val COLUMN_ID_QUERY = "$COLUMN_ID SERIAL PRIMARY KEY, "
        private const val COLUMN_NAME_QUERY = "$COLUMN_NAME VARCHAR(20), "
        private const val COLUMN_DESCRIPTION_QUERY = "$COLUMN_DESCRIPTION VARCHAR(30), "
        private const val COLUMN_DATE_QUERY = "$COLUMN_DATE VARCHAR(20), "
        private const val COLUMN_ROLE_QUERY = "$COLUMN_ROLE INTEGER NOT NULL, "
        private const val COLUMN_ACTION_QUERY = "$COLUMN_ACTION INTEGER NOT NULL"

        val listColumnsQuery = listOf(
            COLUMN_ID_QUERY,
            COLUMN_NAME_QUERY,
            COLUMN_DESCRIPTION_QUERY,
            COLUMN_DATE_QUERY,
            COLUMN_ROLE_QUERY,
            COLUMN_ACTION_QUERY
        )

        val listColumns = listOf(
            COLUMN_ID,
            COLUMN_NAME,
            COLUMN_DESCRIPTION,
            COLUMN_DATE,
            COLUMN_ROLE,
            COLUMN_ACTION
        )
    }

    init {
        try {
            val statement = connection.createStatement()
            statement.executeUpdate(SchemaUtils.createTable( TABLE, listColumnsQuery))
        } catch (e: SQLException) {
            println(e.toString())
        }
    }

    private fun getResultSet(pResultSet: ResultSet ): Permission {
        return Permission(
            pResultSet.getInt( COLUMN_ID ),
            pResultSet.getString( COLUMN_NAME ),
            pResultSet.getString( COLUMN_DESCRIPTION ),
            pResultSet.getString( COLUMN_DATE ),
            pResultSet.getInt( COLUMN_ROLE ),
            pResultSet.getInt( COLUMN_ACTION )
        )
    }

    private fun getPreparedStatement(pPreparedStatement: PreparedStatement, pObj: Any): PreparedStatement{
        pObj as Permission
        pPreparedStatement.setString(1, pObj.permissionName)
        pPreparedStatement.setString(2, pObj.permissionDescription)
        pPreparedStatement.setString(3, getCurrentDate())
        pPreparedStatement.setInt(4, pObj.permission_roleId)
        pPreparedStatement.setInt(5, pObj.permission_actionId)
        return pPreparedStatement
    }

    // Create new permission
    override suspend fun create( obj: Any ): Int = withContext(Dispatchers.IO) {

        val statement = connection.prepareStatement(
            SchemaUtils.insertQuery(TABLE, listColumns),
            Statement.RETURN_GENERATED_KEYS
        )

        obj as Permission

        val statementPos = getPreparedStatement( statement, obj )
        statementPos.executeUpdate()

        val generatedKeys = statementPos.generatedKeys

        if (generatedKeys.next()) {
            return@withContext generatedKeys.getInt(1)
        } else {
            throw Exception(SchemaUtils.UNABLE_NEW_ID_INSERTED)
        }
    }

    // Read a permission
    override suspend fun read(id: Int): Permission = withContext(Dispatchers.IO) {

        val statement = connection.prepareStatement(SchemaUtils.selectQuery(TABLE, COLUMN_ID, listColumns))

        statement.setInt(1, id)

        val resultSet = statement.executeQuery()

        if (resultSet.next()) {
            return@withContext getResultSet( resultSet )
        } else {
            throw Exception(SchemaUtils.RECORD_NOT_FOUND)
        }
    }

    // Update a permission
    override suspend fun update( id: Int, obj: Any ) = withContext(Dispatchers.IO) {

        val statement = connection.prepareStatement(SchemaUtils.updateQuery(TABLE, listColumns, COLUMN_ID) )

        obj as Permission

        val statementPos = getPreparedStatement( statement, obj )
        statementPos.setInt(0, id)
        statementPos.executeUpdate()
    }

    // Delete a permission
    override suspend fun delete(id: Int) = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement("DELETE FROM $TABLE WHERE $COLUMN_ID = ?;")
        statement.setInt(1, id)
        statement.executeUpdate()
    }

    // List all permissions
    override suspend fun list(): List<Permission> = withContext(Dispatchers.IO) {

        val statement = connection.prepareStatement( "SELECT * FROM $TABLE" )

        val resultSet = statement.executeQuery()

        val permissionList = mutableListOf<Permission>()

        while (resultSet.next()) {
            permissionList.add( getResultSet(resultSet) )
        }

        if (permissionList.isNotEmpty()) {
            return@withContext permissionList
        } else {
            throw Exception(SchemaUtils.RECORD_NOT_FOUND)
        }
    }
}