package fernandocostagomes.schemas

import fernandocostagomes.schemas.SchemaUtils.Companion.getCurrentDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import java.sql.Connection
import java.sql.SQLException
import java.sql.Statement

@Serializable
data class Pwd(
    val idPwd: Int = 0,
    val idUserPwd: Int,
    val currentPwd: String,
    val lastPwd: String,
    val moreLastPwd: String,
    val datePwd: String)
class ServicePwd(private val connection: Connection): SchemaInterface {
    companion object {
        private const val TABLE = "v_pwd"
        private const val COLUMN_ID = "v_pwd_id"
        private const val COLUMN_ID_USER = "v_pwd_id_user"
        private const val COLUMN_CURRENT = "v_pwd_current"
        private const val COLUMN_LAST = "v_pwd_last"
        private const val COLUMN_MORE_LAST = "v_pwd_more_last"
        private const val COLUMN_DATE = "v_pwd_date"

        private const val COLUMN_ID_QUERY = "$COLUMN_ID SERIAL PRIMARY KEY, "
        private const val COLUMN_ID_USER_QUERY = "$COLUMN_ID_USER INTEGER NOT NULL, "
        private const val COLUMN_CURRENT_QUERY = "$COLUMN_CURRENT VARCHAR(20) NOT NULL, "
        private const val COLUMN_LAST_QUERY = "$COLUMN_LAST VARCHAR(20), "
        private const val COLUMN_MORE_LAST_QUERY = "$COLUMN_MORE_LAST VARCHAR(20), "
        private const val COLUMN_DATE_QUERY = "$COLUMN_DATE VARCHAR(20)"

        val listColumnsQuery = listOf(COLUMN_ID_QUERY, COLUMN_ID_USER_QUERY,COLUMN_CURRENT_QUERY, COLUMN_LAST_QUERY, 
            COLUMN_MORE_LAST_QUERY, COLUMN_DATE_QUERY)

        val listColumns = listOf(COLUMN_ID, COLUMN_ID_USER, COLUMN_CURRENT, COLUMN_LAST, COLUMN_MORE_LAST, COLUMN_DATE)
    }

    init {
        try {
            val statement = connection.createStatement()
            statement.executeUpdate(SchemaUtils.createTable( TABLE, listColumnsQuery))
        } catch (e: SQLException) {
            println(e.toString())
        }
    }

    // Create new pwd
    override suspend fun create( obj: Any ): Int = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(SchemaUtils.insertQuery(TABLE, listColumns), Statement.RETURN_GENERATED_KEYS)
        obj as Pwd
        statement.setInt(1, obj.idUserPwd )
        statement.setString(2, obj.currentPwd )
        statement.setString(3, obj.lastPwd )
        statement.setString(4, obj.moreLastPwd )
        statement.setString(5, getCurrentDate() )
        statement.executeUpdate()

        val generatedKeys = statement.generatedKeys
        if (generatedKeys.next()) {
            return@withContext generatedKeys.getInt(1)
        } else {
            throw Exception(SchemaUtils.UNABLE_NEW_ID_INSERTED)
        }
    }

    // Read a pwd
    override suspend fun read(id: Int): Pwd = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(SchemaUtils.selectQuery(TABLE, COLUMN_ID_USER, listColumns))
        statement.setInt(1, id)
        val resultSet = statement.executeQuery()

        if (resultSet.next()) {
            val idPwd = resultSet.getInt( COLUMN_ID )
            val idUserPwd = resultSet.getInt( COLUMN_ID_USER )
            val currentPwd = resultSet.getString( COLUMN_CURRENT )
            val lastPwd = resultSet.getString( COLUMN_LAST )
            val moreLastPwd = resultSet.getString( COLUMN_MORE_LAST )
            val datePwd = resultSet.getString( COLUMN_DATE )
            return@withContext Pwd(
                idPwd,
                idUserPwd,
                currentPwd,
                lastPwd,
                moreLastPwd,
                datePwd,
            )
        } else {
            throw Exception(SchemaUtils.RECORD_NOT_FOUND)
        }
    }

    // Update a pwd
    override suspend fun update( id: Int, obj: Any ) = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(SchemaUtils.updateQuery(TABLE, listColumns, COLUMN_ID) )
        obj as Pwd
        statement.setInt(0, id)
        statement.setInt(1, obj.idUserPwd )
        statement.setString(2, obj.currentPwd )
        statement.setString(3, obj.lastPwd )
        statement.setString(4, obj.moreLastPwd )
        statement.setString(5, getCurrentDate() )
        statement.executeUpdate()
    }

    // Delete a pwd
    override suspend fun delete(id: Int) = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement("DELETE FROM $TABLE WHERE $COLUMN_ID = ?;")
        statement.setInt(1, id)
        statement.executeUpdate()
    }

    // List all pwds
    override suspend fun list(): List<Pwd> = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement( "SELECT * FROM $TABLE" )
        val resultSet = statement.executeQuery()

        val pwdList = mutableListOf<Pwd>()

        while (resultSet.next()) {

            val idPwd = resultSet.getInt( COLUMN_ID )
            val idUserPwd = resultSet.getInt( COLUMN_ID_USER )
            val currentPwd = resultSet.getString( COLUMN_CURRENT )
            val lastPwd = resultSet.getString( COLUMN_LAST )
            val moreLastPwd = resultSet.getString( COLUMN_MORE_LAST )
            val datePwd = resultSet.getString( COLUMN_DATE )

            pwdList.add(
                Pwd(
                    idPwd,
                    idUserPwd,
                    currentPwd,
                    lastPwd,
                    moreLastPwd,
                    datePwd
                )
            )
        }

        if (pwdList.isNotEmpty()) {
            return@withContext pwdList
        } else {
            throw Exception(SchemaUtils.RECORD_NOT_FOUND)
        }
    }
}