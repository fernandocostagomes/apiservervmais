package fernandocostagomes.schemas

import kotlinx.coroutines.*
import kotlinx.serialization.Serializable
import java.sql.*

@Serializable
data class Group(
    val groupId: Int,
    var groupName: String,
    var groupPwd: String,
    var groupDate: String,
    val groupUserId: Int)
class ServiceGroup(private val connection: Connection): SchemaInterface {
    companion object {
        private const val TABLE = "v_group"
        private const val COLUMN_ID = "v_group_id"
        private const val COLUMN_NAME = "v_group_name"
        private const val COLUMN_PWD = "v_group_pwd"
        private const val COLUMN_DATE = "v_group_date"
        private const val COLUMN_USER_ID = "v_user_id"

        private const val COLUMN_ID_QUERY = "$COLUMN_ID SERIAL PRIMARY KEY, "
        private const val COLUMN_NAME_QUERY = "$COLUMN_NAME VARCHAR(20), "
        private const val COLUMN_PWD_QUERY = "$COLUMN_PWD VARCHAR(8) NOT NULL, "
        private const val COLUMN_DATE_QUERY = "$COLUMN_DATE VARCHAR(20)NOT NULL, "
        private const val COLUMN_USER_ID_QUERY = "$COLUMN_USER_ID INTEGER"

        val listColumnsQuery = listOf(
            COLUMN_ID_QUERY,
            COLUMN_NAME_QUERY,
            COLUMN_PWD_QUERY,
            COLUMN_DATE_QUERY,
            COLUMN_USER_ID_QUERY
        )

        val listColumns = listOf(
            COLUMN_ID,
            COLUMN_NAME,
            COLUMN_PWD,
            COLUMN_DATE,
            COLUMN_USER_ID
        )
    }

    init {
        try {
            val statement = connection.createStatement()
            statement.executeUpdate( SchemaUtils.createTable(TABLE, listColumnsQuery))
        } catch (e: SQLException) {
            println(e.toString())
        }
    }

    private fun getResultset(pResultSet: ResultSet): Group {
        return Group(
            pResultSet.getInt(COLUMN_ID),
            pResultSet.getString(COLUMN_NAME),
            pResultSet.getString(COLUMN_PWD),
            pResultSet.getString(COLUMN_DATE),
            pResultSet.getInt(COLUMN_USER_ID)
        )
    }

    private fun getPreparedStatement(pPreparedStatement: PreparedStatement, pObj: Any): PreparedStatement {
        pObj as Group
        pPreparedStatement.setString(1, pObj.groupName)
        pPreparedStatement.setString(2, pObj.groupPwd)
        pPreparedStatement.setString(3, SchemaUtils.getCurrentDate())
        pPreparedStatement.setInt(4, pObj.groupUserId)
        return pPreparedStatement
    }

    // Create new group
    override suspend fun create(obj: Any): Int = withContext(Dispatchers.IO) {

        val statement = connection.prepareStatement(
            SchemaUtils.insertQuery(
                TABLE,
                listColumns
            ), Statement.RETURN_GENERATED_KEYS)

        obj as Group

        val statementPos: PreparedStatement = getPreparedStatement( statement, obj )
        statementPos.executeUpdate()

        val generatedKeys = statementPos.generatedKeys

        if (generatedKeys.next()) {
            return@withContext generatedKeys.getInt(1)
        } else {
            throw Exception("Unable to retrieve the id of the newly inserted group")
        }
    }

    // Read a group
    override suspend fun read(id: Int): Group = withContext(Dispatchers.IO) {

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
            return@withContext getResultset( resultSet )
        } else {
            throw Exception("Record not found")
        }
    }

    // Update a group
    override suspend fun update(id: Int, obj: Any) = withContext(Dispatchers.IO) {

        val statement = connection.prepareStatement(
            SchemaUtils.updateQuery(
                TABLE,
                listColumns,
                COLUMN_ID
            )
        )

        obj as Group

        val statementPos = getPreparedStatement( statement, obj )
        statementPos.setInt(0, id)
        statementPos.executeUpdate()
    }

    // Delete a group
    override suspend fun delete(id: Int) = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement( "DELETE FROM $TABLE WHERE $COLUMN_ID = ?" )
        statement.setInt(1, id)
        statement.executeUpdate()
    }

    override suspend fun list(): List<Group> = withContext(Dispatchers.IO) {

        val statement = connection.prepareStatement( "SELECT * FROM $TABLE" )

        val resultSet = statement.executeQuery()

        val groupList = mutableListOf<Group>()

        while (resultSet.next()) {
            groupList.add(getResultset( resultSet ))
        }

        if (groupList.isNotEmpty()) {
            return@withContext groupList
        } else {
            throw Exception("No records found")
        }
    }
}