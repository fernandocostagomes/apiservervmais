package fernandocostagomes.models

import kotlinx.coroutines.*
import kotlinx.serialization.Serializable
import java.sql.Connection
import java.sql.SQLException
import java.sql.Statement

@Serializable
data class Group(
    val groupName: String,
    val groupPwd: String,
    val groupDate: String,
    val userId: Int)
class ServiceGroup(private val connection: Connection): SchemaInterface {
    companion object {
        private const val TABLE = "group"
        private const val COLUMN_ID = "group_id"
        private const val COLUMN_NAME = "group_name"
        private const val COLUMN_PWD = "group_name"
        private const val COLUMN_DATE = "group_date"
        private const val COLUMN_USER_ID = "user_id"

        private const val CREATE_TABLE_GROUP = "CREATE TABLE IF NOT EXISTS " +
                "$TABLE (" +
                "$COLUMN_ID SERIAL PRIMARY KEY, " +
                "$COLUMN_NAME VARCHAR(20), " +
                "$COLUMN_PWD VARCHAR(8) NOT NULL, " +
                "$COLUMN_DATE VARCHAR(16)NOT NULL, " +
                "$COLUMN_USER_ID INTEGER)"

        private const val SELECT_GROUP_BY_ID = "SELECT " +
                "$COLUMN_NAME, " +
                "$COLUMN_PWD, " +
                "$COLUMN_DATE, " +
                "$COLUMN_USER_ID " +
                "FROM $TABLE WHERE $COLUMN_ID = ?"

        private const val INSERT_GROUP = "INSERT INTO $TABLE (" +
                "$COLUMN_NAME, " +
                "$COLUMN_PWD, " +
                "$COLUMN_DATE, " +
                "$COLUMN_USER_ID) VALUES (?, ?, ?, ?)"

        private const val UPDATE_GROUP = "UPDATE $TABLE SET " +
                "$COLUMN_NAME = ?," +
                "$COLUMN_PWD = ?," +
                "$COLUMN_DATE = ? WHERE $COLUMN_ID = ?"

        private const val DELETE_GROUP = "DELETE FROM $TABLE WHERE $COLUMN_ID = ?"

        private const val LIST_GROUP = "SELECT * FROM $TABLE"
    }

    init {
        try {
            val statement = connection.createStatement()
            statement.executeUpdate(CREATE_TABLE_GROUP)
        } catch (e: SQLException) {
            println(e.toString())
        }
    }

    // Create new group
    override suspend fun create(obj: Any): Int = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(INSERT_GROUP, Statement.RETURN_GENERATED_KEYS)
        obj as Group
        statement.setString(1, obj.groupName)
        statement.setString(2, obj.groupPwd)
        statement.setString(3, obj.groupDate)
        statement.setInt(4, obj.userId)
        statement.executeUpdate()

        val generatedKeys = statement.generatedKeys
        if (generatedKeys.next()) {
            return@withContext generatedKeys.getInt(1)
        } else {
            throw Exception("Unable to retrieve the id of the newly inserted group")
        }
    }

    // Read a group
    override suspend fun read(id: Int): Group = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(SELECT_GROUP_BY_ID)
        statement.setInt(1, id)
        val resultSet = statement.executeQuery()

        if (resultSet.next()) {
            val name = resultSet.getString(COLUMN_NAME)
            val pwd = resultSet.getString(COLUMN_PWD)
            val date = resultSet.getString(COLUMN_DATE)
            val idUser = resultSet.getInt(COLUMN_USER_ID)
            return@withContext Group(name, pwd, date, idUser)
        } else {
            throw Exception("Record not found")
        }
    }

    // Update a group
    override suspend fun update(id: Int, obj: Any) = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(UPDATE_GROUP)
        obj as Group
        statement.setInt(0, id)
        statement.setString(1, obj.groupName)
        statement.setString(2, obj.groupPwd)
        statement.setString(3, obj.groupDate)
        statement.setInt(4, obj.userId)
        statement.executeUpdate()
    }

    // Delete a group
    override suspend fun delete(id: Int) = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(DELETE_GROUP)
        statement.setInt(1, id)
        statement.executeUpdate()
    }

    override suspend fun list(): List<Group> = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(LIST_GROUP)
        val resultSet = statement.executeQuery()

        val groupList = mutableListOf<Group>()

        while (resultSet.next()) {
            val name = resultSet.getString(COLUMN_NAME)
            val pwd = resultSet.getString(COLUMN_PWD)
            val date = resultSet.getString(COLUMN_DATE)
            val idUser = resultSet.getInt(COLUMN_USER_ID)

            val group = Group(name, pwd, date, idUser)
            groupList.add(group)
        }

        if (groupList.isNotEmpty()) {
            return@withContext groupList
        } else {
            throw Exception("No records found")
        }
    }
}