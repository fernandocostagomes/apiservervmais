package fernandocostagomes.schemas

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import java.sql.*

@Serializable
data class Action(
    val actionId: Int = 0,
    val actionName: String,
    val actionDescription: String,
    val actionDate: String)
class ServiceAction(private val connection: Connection): SchemaInterface {
    companion object {
        private const val TABLE = "v_action"
        private const val COLUMN_ID = "v_action_id"
        private const val COLUMN_NAME = "v_action_name"
        private const val COLUMN_DESCRIPTION = "v_action_description"
        private const val COLUMN_DATE = "v_action_date"

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

    private fun getResultset(pResultSet: ResultSet): Action {
        return Action(
            pResultSet.getInt( COLUMN_ID ),
            pResultSet.getString( COLUMN_NAME ),
            pResultSet.getString( COLUMN_DESCRIPTION ),
            pResultSet.getString( COLUMN_DATE )
        )
    }

    private fun getStatement(pStatement: PreparedStatement, pObj: Any): PreparedStatement {
        pObj as Action
        pStatement.setString(1, pObj.actionName)
        pStatement.setString(2, pObj.actionDescription)
        pStatement.setString(3, SchemaUtils.getCurrentDate())
        return pStatement
    }

    // Create new action
    override suspend fun create( obj: Any ): Int = withContext( Dispatchers.IO ) {

        val statement = connection.prepareStatement(
            SchemaUtils.insertQuery(TABLE, listColumns),
            Statement.RETURN_GENERATED_KEYS
        )

        obj as Action

        val statementPos: PreparedStatement = getStatement( statement, obj )
        statementPos.executeUpdate()

        val generatedKeys = statementPos.generatedKeys

        if (generatedKeys.next()) {
            return@withContext generatedKeys.getInt(1)
        } else {
            throw Exception(SchemaUtils.UNABLE_NEW_ID_INSERTED)
        }
    }

    // Read an action
    override suspend fun read(id: Int): Action = withContext( Dispatchers.IO ) {

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
            throw Exception(SchemaUtils.RECORD_NOT_FOUND)
        }
    }

    // Update an action
    override suspend fun update( id: Int, obj: Any ) = withContext( Dispatchers.IO ) {

        val statement = connection.prepareStatement(
            SchemaUtils.updateQuery(
                TABLE,
                listColumns,
                COLUMN_ID
            )
        )

        obj as Action

        val statementPos: PreparedStatement = getStatement( statement, obj )
        statementPos.setInt(0, id)
        statementPos.executeUpdate()
    }

    // Delete an action
    override suspend fun delete(id: Int) = withContext( Dispatchers.IO ) {
        val statement = connection.prepareStatement("DELETE FROM $TABLE WHERE $COLUMN_ID = ?;")
        statement.setInt(1, id)
        statement.executeUpdate()
    }

    // List all actions
    override suspend fun list(): List<Action> = withContext( Dispatchers.IO ) {

        val statement = connection.prepareStatement( "SELECT * FROM $TABLE" )

        val resultSet = statement.executeQuery()

        val actionList = mutableListOf<Action>()

        while (resultSet.next()) {
            actionList.add( getResultset( resultSet ) )
        }

        if (actionList.isNotEmpty()) {
            return@withContext actionList
        } else {
            throw Exception(SchemaUtils.RECORD_NOT_FOUND)
        }
    }
}