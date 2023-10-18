package fernandocostagomes.schemas

import kotlinx.coroutines.*
import kotlinx.serialization.Serializable
import java.sql.Connection
import java.sql.SQLException
import java.sql.Statement

@Serializable
data class User(
    val userId: Int = 0,
    val userEmail: String,
    val userName: String,
    val userPwd: String,
    val userPhone: String,
    val userNick: String,
    val userBirthday: String,
    val userDate: String)

class ServiceUser(private val connection: Connection) : SchemaInterface {
    companion object {
        private const val TABLE = "v_user"
        private const val COLUMN_ID = "v_user_id"
        private const val COLUMN_EMAIL = "v_user_email"
        private const val COLUMN_NAME = "v_user_name"
        private const val COLUMN_PWD = "v_user_pwd"
        private const val COLUMN_PHONE = "v_user_phone"
        private const val COLUMN_NICK = "v_user_nick"
        private const val COLUMN_BIRTHDAY = "v_user_birthday"
        private const val COLUMN_DATE = "v_user_date"


        private const val COLUMN_ID_QUERY = "$COLUMN_ID SERIAL PRIMARY KEY, "
        private const val COLUMN_EMAIL_QUERY = "$COLUMN_EMAIL VARCHAR(50) NOT NULL, "
        private const val COLUMN_NAME_QUERY = "$COLUMN_NAME VARCHAR(30), "
        private const val COLUMN_PWD_QUERY = "$COLUMN_PWD VARCHAR(8) NOT NULL, "
        private const val COLUMN_PHONE_QUERY = "$COLUMN_PHONE VARCHAR(13) NOT NULL, "
        private const val COLUMN_NICK_QUERY = "$COLUMN_NICK VARCHAR(20), "
        private const val COLUMN_BIRTHDAY_QUERY = "$COLUMN_BIRTHDAY VARCHAR(20), "
        private const val COLUMN_DATE_QUERY = "$COLUMN_DATE VARCHAR(20)"

        val listColumnsQuery = listOf(
            COLUMN_ID_QUERY,
            COLUMN_EMAIL_QUERY,
            COLUMN_NAME_QUERY,
            COLUMN_PWD_QUERY,
            COLUMN_PHONE_QUERY,
            COLUMN_NICK_QUERY,
            COLUMN_BIRTHDAY_QUERY,
            COLUMN_DATE_QUERY
        )

        val listColumns = listOf(
            COLUMN_ID,
            COLUMN_EMAIL,
            COLUMN_NAME,
            COLUMN_PWD,
            COLUMN_PHONE,
            COLUMN_NICK,
            COLUMN_BIRTHDAY,
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

    // Create new user
    override suspend fun create( obj: Any ): Int = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(
            SchemaUtils.insertQuery(
                TABLE,
                listColumns
            ),
            Statement.RETURN_GENERATED_KEYS)
        obj as User
        statement.setString(1, obj.userEmail)
        statement.setString(2, obj.userName)
        statement.setString(3, obj.userPwd)
        statement.setString(4, obj.userPhone)
        statement.setString(5, obj.userNick)
        statement.setString(6, obj.userBirthday)
        statement.setString(7, SchemaUtils.getCurrentDate())
        statement.executeUpdate()

        val generatedKeys = statement.generatedKeys
        if (generatedKeys.next()) {
            return@withContext generatedKeys.getInt(1)
        } else {
            throw Exception(SchemaUtils.UNABLE_NEW_ID_INSERTED)
        }
    }

    // Read a user
    override suspend fun read(id: Int): User = withContext(Dispatchers.IO) {
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
            val idUser = resultSet.getInt(COLUMN_ID)
            val emailUser = resultSet.getString(COLUMN_EMAIL)
            val nameUser = resultSet.getString(COLUMN_NAME)
            val pwdUser = resultSet.getString(COLUMN_PWD)
            val phoneUser = resultSet.getString(COLUMN_PHONE)
            val nickUser = resultSet.getString(COLUMN_NICK)
            val birthdayUser = resultSet.getString(COLUMN_BIRTHDAY)
            val dateUser = resultSet.getString(COLUMN_DATE)

            return@withContext User(
                idUser,
                emailUser,
                nameUser,
                pwdUser,
                phoneUser,
                nickUser,
                birthdayUser,
                dateUser)
        } else {
            throw Exception(SchemaUtils.RECORD_NOT_FOUND)
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
        obj as User
        statement.setInt(0, id)
        statement.setString(1, obj.userEmail)
        statement.setString(2, obj.userName)
        statement.setString(3, obj.userPwd)
        statement.setString(4, obj.userPhone)
        statement.setString(5, obj.userNick)
        statement.setString(6, SchemaUtils.getCurrentDate())
        statement.executeUpdate()
    }

    // Delete a user
    override suspend fun delete(id: Int) = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement( "DELETE FROM $TABLE WHERE $COLUMN_ID = ?" )
        statement.setInt(1, id)
        statement.executeUpdate()
    }

    override suspend fun list(): List<User> = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement( "SELECT * FROM $TABLE" )
        val resultSet = statement.executeQuery()

        val userList = mutableListOf<User>()

        while (resultSet.next()) {
            val idUser = resultSet.getInt(COLUMN_ID)
            val emailUser = resultSet.getString(COLUMN_EMAIL)
            val nameUser = resultSet.getString(COLUMN_NAME)
            val pwdUser = resultSet.getString(COLUMN_PWD)
            val phoneUser = resultSet.getString(COLUMN_PHONE)
            val nickUser = resultSet.getString(COLUMN_NICK)
            val birthday = resultSet.getString(COLUMN_BIRTHDAY)
            val dateUser = resultSet.getString(COLUMN_DATE)

            userList.add(
                User(
                    idUser,
                    emailUser,
                    nameUser,
                    pwdUser,
                    phoneUser,
                    nickUser,
                    birthday,
                    dateUser
                )
            )
        }

        if (userList.isNotEmpty()) {
            return@withContext userList
        } else {
            throw Exception(SchemaUtils.RECORD_NOT_FOUND)
        }
    }
}