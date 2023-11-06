package fernandocostagomes.schemas

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Statement

@Serializable
data class Pwd(
    val pwdId: Int = 0,
    val pwdUserId: Int,
    var pwdCurrent: String,
    var pwdLast: String,
    var pwdMoreLast: String,
    var pwdDate: String)
class ServicePwd(private val connection: Connection): SchemaInterface {
    companion object {
        private const val TABLE = "v_pwd"
        private const val COLUMN_ID = "v_pwd_id"
        private const val COLUMN_ID_USER = "v_user_id"
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

        val listColumnsQuery = listOf(
            COLUMN_ID_QUERY,
            COLUMN_ID_USER_QUERY,
            COLUMN_CURRENT_QUERY,
            COLUMN_LAST_QUERY,
            COLUMN_MORE_LAST_QUERY,
            COLUMN_DATE_QUERY
        )

        val listColumns = listOf(
            COLUMN_ID,
            COLUMN_ID_USER,
            COLUMN_CURRENT,
            COLUMN_LAST,
            COLUMN_MORE_LAST,
            COLUMN_DATE
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

    private fun getResultSet(pResultSet: ResultSet): Pwd {
        return Pwd(
            pResultSet.getInt(COLUMN_ID),
            pResultSet.getInt(COLUMN_ID_USER),
            pResultSet.getString(COLUMN_CURRENT),
            pResultSet.getString(COLUMN_LAST),
            pResultSet.getString(COLUMN_MORE_LAST),
            pResultSet.getString(COLUMN_DATE)
        )
    }

    private fun getPreparedStatement(pPreparedStatement: PreparedStatement, pObj: Any): PreparedStatement{
        pObj as Pwd
        pPreparedStatement.setInt(1, pObj.pwdUserId)
        pPreparedStatement.setString(2, pObj.pwdCurrent)
        pPreparedStatement.setString(3, pObj.pwdLast)
        pPreparedStatement.setString(4, pObj.pwdMoreLast)
        pPreparedStatement.setString(5, SchemaUtils.getCurrentDate())
        return pPreparedStatement
    }

    // Create new pwd
    override suspend fun create( obj: Any ): Int = withContext(Dispatchers.IO) {

        val query = SchemaUtils.insertQuery( TABLE, listColumns )
        println( query )

        val statement = connection.prepareStatement( query, Statement.RETURN_GENERATED_KEYS)

        //Imprimi todas as tabelas que o statement recebeu como parametro.
        println( statement.toString() )

        obj as Pwd

        val statementPos: PreparedStatement = getPreparedStatement( statement, obj )
        statementPos.executeUpdate()

        val generatedKeys = statementPos.generatedKeys

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
            return@withContext getResultSet( resultSet )
        } else {
            throw Exception(SchemaUtils.RECORD_NOT_FOUND)
        }
    }

    // Update a pwd
    override suspend fun update( id: Int, obj: Any ) = withContext(Dispatchers.IO) {

        val statement = connection.prepareStatement(SchemaUtils.updateQuery(TABLE, listColumns, COLUMN_ID) )

        obj as Pwd

        val statementPos = getPreparedStatement( statement, obj )
        statementPos.setInt(0, id)
        statementPos.executeUpdate()
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
            pwdList.add( getResultSet( resultSet ) )
        }

        if (pwdList.isNotEmpty()) {
            return@withContext pwdList
        } else {
            throw Exception(SchemaUtils.RECORD_NOT_FOUND)
        }
    }
}