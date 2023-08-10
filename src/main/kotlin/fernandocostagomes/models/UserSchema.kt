package fernandocostagomes.models

import kotlinx.coroutines.*
import kotlinx.serialization.Serializable
import java.sql.Connection
import java.sql.SQLException
import java.sql.Statement

@Serializable
data class User(val userDate: String,
                val userEmail: String,
                val userPhone: String,
                val userPwd: String)

class UserService(private val connection: Connection) {
    companion object {
        private const val TABLE = "user"
        private const val COLUMN_ID = "user_id"
        private const val COLUMN_DATE = "user_date"
        private const val COLUMN_EMAIL = "user_email"
        private const val COLUMN_PHONE = "user_phone"
        private const val COLUMN_PWD = "user_pwd"

        private const val CREATE_TABLE_USER =
            "CREATE TABLE IF NOT EXISTS $TABLE(" +
                "$COLUMN_ID SERIAL PRIMARY KEY, " +
                "$COLUMN_DATE VARCHAR(20), " +
                "$COLUMN_EMAIL VARCHAR(50) NOT NULL, " +
                "$COLUMN_PHONE VARCHAR(11) NOT NULL, " +
                "$COLUMN_PWD VARCHAR(8) NOT NULL);"

        private const val SELECT_USER_BY_ID = "SELECT " +
                "$COLUMN_DATE, " +
                "$COLUMN_EMAIL, " +
                "$COLUMN_PHONE, " +
                "$COLUMN_PWD " +
                "FROM $TABLE WHERE $COLUMN_ID = ?"

        private const val INSERT_USER = "INSERT INTO $TABLE (" +
                "$COLUMN_DATE, " +
                "$COLUMN_EMAIL, " +
                "$COLUMN_PHONE, " +
                "$COLUMN_PWD) " +
                "VALUES (?, ?, ?, ?)"

        private const val UPDATE_USER = "UPDATE $TABLE SET " +
                "$COLUMN_DATE = ?," +
                "$COLUMN_EMAIL = ?," +
                "$COLUMN_PHONE = ?," +
                "$COLUMN_PWD = ? WHERE $COLUMN_ID = ?"

        private const val DELETE_USER = "DELETE FROM $TABLE WHERE $COLUMN_ID = ?"

        private const val LIST_USER = "SELECT * FROM $TABLE"
    }

    init {
        try {
            val statement = connection.createStatement()
            statement.executeUpdate(CREATE_TABLE_USER)
        } catch (e: SQLException) {
            println(e.toString())
        }
    }

    private var newUserId = 0

    // Create new user
    suspend fun create(user: User): Int = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(INSERT_USER, Statement.RETURN_GENERATED_KEYS)
        statement.setString(1, user.userDate)
        statement.setString(2, user.userEmail)
        statement.setString(3, user.userPhone)
        statement.setString(4, user.userPwd)
        statement.executeUpdate()

        val generatedKeys = statement.generatedKeys
        if (generatedKeys.next()) {
            return@withContext generatedKeys.getInt(1)
        } else {
            throw Exception("Unable to retrieve the id of the newly inserted user")
        }
    }

    // Read a user
    suspend fun read(id: Int): User = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(SELECT_USER_BY_ID)
        statement.setInt(1, id)
        val resultSet = statement.executeQuery()

        if (resultSet.next()) {
            val dateUser = resultSet.getString(COLUMN_DATE)
            val emailUser = resultSet.getString(COLUMN_EMAIL)
            val phoneUser = resultSet.getString(COLUMN_PHONE)
            val passwordUser = resultSet.getString(COLUMN_PWD)

            return@withContext User(
                dateUser,
                emailUser,
                phoneUser,
                passwordUser)
        } else {
            throw Exception("Record not found")
        }
    }

    // Update a user
    suspend fun update(id: Int, user: User) = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(UPDATE_USER)
        statement.setInt(0, id)
        statement.setString(1, user.userDate)
        statement.setString(2, user.userEmail)
        statement.setString(3, user.userPhone)
        statement.setString(4, user.userPwd)
        statement.executeUpdate()
    }

    // Delete a user
    suspend fun delete(id: Int) = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(DELETE_USER)
        statement.setInt(1, id)
        statement.executeUpdate()
    }

    suspend fun list(): List<User> = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(LIST_USER)
        val resultSet = statement.executeQuery()

        val userList = mutableListOf<User>()

        while (resultSet.next()) {
            val dateUser = resultSet.getString(COLUMN_DATE)
            val emailUser = resultSet.getString(COLUMN_EMAIL)
            val phoneUser = resultSet.getString(COLUMN_PHONE)
            val pwdUser = resultSet.getString(COLUMN_PWD)

            val user = User(
                dateUser,
                emailUser,
                phoneUser,
                pwdUser)
            userList.add(user)
        }

        if (userList.isNotEmpty()) {
            return@withContext userList
        } else {
            throw Exception("No records found")
        }
    }
}