package fernandocostagomes.schemas

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import java.sql.Connection
import java.sql.SQLException
import java.sql.Statement

@Serializable
data class Permission(
    val idPermission: Int,
    val namePermission: String,
    val descriptionPermission: String,
    val datePermission: String,
    val idRolePermission: Int,
    val idActionPermission: Int)
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

        val listColumnsQuery = listOf(COLUMN_ID_QUERY, COLUMN_NAME_QUERY, COLUMN_DESCRIPTION_QUERY,
            COLUMN_DATE_QUERY, COLUMN_ROLE_QUERY, COLUMN_ACTION_QUERY)

        val listColumns = listOf(COLUMN_ID, COLUMN_NAME, COLUMN_DESCRIPTION, COLUMN_DATE, COLUMN_ROLE,
            COLUMN_ACTION)
    }

    init {
        try {
            val statement = connection.createStatement()
            statement.executeUpdate(SchemaUtils.createTable( TABLE, listColumnsQuery))
        } catch (e: SQLException) {
            println(e.toString())
        }
    }

    // Create new permission
    override suspend fun create( obj: Any ): Int = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(SchemaUtils.insertQuery(TABLE, listColumns), Statement.RETURN_GENERATED_KEYS)
        obj as Permission
        statement.setString(1, obj.namePermission)
        statement.setString(2, obj.descriptionPermission)
        statement.setString(3, obj.datePermission)
        statement.setInt(4, obj.idRolePermission)
        statement.setInt(5, obj.idActionPermission)
        statement.executeUpdate()

        val generatedKeys = statement.generatedKeys
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
            val idPermission = resultSet.getInt( COLUMN_ID )
            val namePermission = resultSet.getString( COLUMN_NAME )
            val descriptionPermission = resultSet.getString( COLUMN_DESCRIPTION )
            val datePermission = resultSet.getString( COLUMN_DATE )
            val idRolePermission = resultSet.getInt( COLUMN_ROLE )
            val idActionPermission = resultSet.getInt( COLUMN_ACTION )
            return@withContext Permission(
                idPermission,
                namePermission,
                descriptionPermission,
                datePermission,
                idRolePermission,
                idActionPermission
            )
        } else {
            throw Exception(SchemaUtils.RECORD_NOT_FOUND)
        }
    }

    // Update a permission
    override suspend fun update( id: Int, obj: Any ) = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(SchemaUtils.updateQuery(TABLE, listColumns, COLUMN_ID) )
        obj as Permission
        statement.setInt(0, id)
        statement.setString(1, obj.namePermission)
        statement.setString(2, obj.descriptionPermission)
        statement.setString(3, obj.datePermission)
        statement.setInt(4, obj.idRolePermission)
        statement.setInt(5, obj.idActionPermission)
        statement.executeUpdate()
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

            val idPermission = resultSet.getInt( COLUMN_ID )
            val namePermission = resultSet.getString( COLUMN_NAME )
            val descriptionPermission = resultSet.getString( COLUMN_DESCRIPTION )
            val datePermission = resultSet.getString( COLUMN_DATE )
            val idRolePermission = resultSet.getInt( COLUMN_ROLE )
            val idActionPermission = resultSet.getInt( COLUMN_ACTION )

            permissionList.add(
                Permission(
                    idPermission,
                    namePermission,
                    descriptionPermission,
                    datePermission,
                    idRolePermission,
                    idActionPermission
                )
            )
        }

        if (permissionList.isNotEmpty()) {
            return@withContext permissionList
        } else {
            throw Exception(SchemaUtils.RECORD_NOT_FOUND)
        }
    }
}