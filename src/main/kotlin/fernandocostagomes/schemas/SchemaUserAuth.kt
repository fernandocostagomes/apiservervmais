package fernandocostagomes.schemas

import kotlinx.coroutines.*
import kotlinx.serialization.Serializable
import java.sql.Connection
import java.sql.SQLException
import java.sql.Statement

@Serializable
data class UserAuth(
    val idUserAuth: Int,
    val idUser: Int,
    val userNameAuth: String,
    val pwdUserAuth: String,
    val dateUserAuth: String)

class ServiceUserAuth(private val connection: Connection) : SchemaInterface {
    companion object {
        private const val TABLE = "v_user_auth"
        private const val COLUMN_ID = "v_user_auth_id"
        private const val COLUMN_ID_USER = "v_user_auth_id_user"
        private const val COLUMN_NAME = "v_user_auth_name"
        private const val COLUMN_PWD = "v_user_auth_pwd"
        private const val COLUMN_DATE = "v_user_auth_date"

        private const val COLUMN_ID_QUERY = "$COLUMN_ID SERIAL PRIMARY KEY, "
        private const val COLUMN_ID_USER_QUERY = "$COLUMN_ID_USER INTEGER NOT NULL, "
        private const val COLUMN_NAME_QUERY = "$COLUMN_NAME VARCHAR(20), "
        private const val COLUMN_PWD_QUERY = "$COLUMN_PWD VARCHAR(8) NOT NULL, "
        private const val COLUMN_DATE_QUERY = "$COLUMN_DATE VARCHAR(20), "

        val listColumnsQuery = listOf(
            COLUMN_ID_QUERY,
            COLUMN_ID_USER_QUERY,
            COLUMN_NAME_QUERY,
            COLUMN_PWD_QUERY,
            COLUMN_DATE_QUERY
        )

        val listColumns = listOf(
            COLUMN_ID,
            COLUMN_ID_USER,
            COLUMN_NAME,
            COLUMN_PWD,
            COLUMN_DATE
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

    // Create new userAuth
    override suspend fun create( obj: Any ): Int = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(
            SchemaUtils.insertQuery(
                TABLE,
                listColumnsQuery
            ), Statement.RETURN_GENERATED_KEYS)
        obj as UserAuth
        statement.setString(1, obj.idUser.toString() )
        statement.setString(2, obj.userNameAuth )
        statement.setString(3, obj.pwdUserAuth )
        statement.setString(4, obj.dateUserAuth )
        statement.executeUpdate()

        val generatedKeys = statement.generatedKeys
        if (generatedKeys.next()) {
            return@withContext generatedKeys.getInt(1)
        } else {
            throw Exception("Unable to retrieve the id of the newly inserted user")
        }
    }

    // Read a userAuth
    override suspend fun read(id: Int): UserAuth = withContext(Dispatchers.IO) {
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
            val idUserAuth = resultSet.getString( COLUMN_ID )
            val idUser = resultSet.getString( COLUMN_ID_USER )
            val nameUserAuth = resultSet.getString( COLUMN_NAME )
            val pwdUserAuth = resultSet.getString( COLUMN_PWD )
            val dateUserAuth = resultSet.getString( COLUMN_DATE )

            return@withContext UserAuth(
                idUserAuth.toInt(),
                idUser.toInt(),
                nameUserAuth,
                pwdUserAuth,
                dateUserAuth
            )
        } else {
            throw Exception("Record not found")
        }
    }

    // Update a user
    override suspend fun update( id: Int, obj: Any ) = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(
            SchemaUtils.updateQuery(
                TABLE,
                listColumns,
                COLUMN_ID
            )
        )
        obj as UserAuth
        statement.setInt(0, id)
        statement.setString(1, obj.idUser.toString() )
        statement.setString(2, obj.userNameAuth )
        statement.setString(3, obj.pwdUserAuth )
        statement.setString(4, obj.dateUserAuth )
        statement.executeUpdate()
    }

    // Delete a user
    override suspend fun delete(id: Int) = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement( "DELETE FROM $TABLE WHERE $COLUMN_ID = ?" )
        statement.setInt(1, id)
        statement.executeUpdate()
    }

    override suspend fun list(): List<UserAuth> = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement( "SELECT * FROM $TABLE" )
        val resultSet = statement.executeQuery()

        val userList = mutableListOf<UserAuth>()

        while (resultSet.next()) {
            val idUserAuth = resultSet.getString( COLUMN_ID )
            val idUser = resultSet.getString( COLUMN_ID_USER )
            val nameUserAuth = resultSet.getString( COLUMN_NAME )
            val pwdUserAuth = resultSet.getString( COLUMN_PWD )
            val dateUserAuth = resultSet.getString( COLUMN_DATE )

            val user = UserAuth(
                idUserAuth.toInt(),
                idUser.toInt(),
                nameUserAuth,
                pwdUserAuth,
                dateUserAuth
            )
            userList.add(user)
        }

        if (userList.isNotEmpty()) {
            return@withContext userList
        } else {
            throw Exception("No records found")
        }
    }
}