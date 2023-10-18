package fernandocostagomes.schemas

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import java.sql.Connection
import java.sql.SQLException
import java.sql.Statement

@Serializable
data class Action(
    val idAction: Int = 0,
    val nameAction: String,
    val descriptionAction: String,
    val dateAction: String)
class ServiceAction(private val connection: Connection): SchemaInterface {
    companion object {
        private const val TABLE = "v_action"
        private const val COLUMN_ID = "v_action_id"
        private const val COLUMN_NAME = "v_action_name"
        private const val COLUMN_DESCRIPTION = "v_action_description"
        private const val COLUMN_DATE = "v_action_date"

        private const val COLUMN_ID_QUERY = "$COLUMN_ID SERIAL PRIMARY KEY, "
        private const val COLUMN_NAME_QUERY = "$COLUMN_NAME VARCHAR(20), "
        private const val COLUMN_DESCRIPTION_QUERY = "$COLUMN_DESCRIPTION VARCHAR(30)"
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

    // Create new action
    override suspend fun create( obj: Any ): Int = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(
            SchemaUtils.insertQuery(TABLE, listColumns), Statement.RETURN_GENERATED_KEYS)
        obj as Action
        statement.setString(1, obj.nameAction)
        statement.setString(2, obj.descriptionAction)
        statement.setString(3, SchemaUtils.getCurrentDate())
        statement.executeUpdate()

        val generatedKeys = statement.generatedKeys
        if (generatedKeys.next()) {
            return@withContext generatedKeys.getInt(1)
        } else {
            throw Exception(SchemaUtils.UNABLE_NEW_ID_INSERTED)
        }
    }

    // Read an action
    override suspend fun read(id: Int): Action = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(
            SchemaUtils.selectQuery(TABLE, COLUMN_ID, listColumns)
        )
        statement.setInt(1, id)
        val resultSet = statement.executeQuery()

        if (resultSet.next()) {
            val idAction = resultSet.getInt(COLUMN_ID)
            val nameAction = resultSet.getString(COLUMN_NAME)
            val descriptionAction = resultSet.getString(COLUMN_DESCRIPTION)
            val dateAction = resultSet.getString(COLUMN_DATE)
            return@withContext Action(
                idAction,
                nameAction,
                descriptionAction,
                dateAction
            )
        } else {
            throw Exception(SchemaUtils.RECORD_NOT_FOUND)
        }
    }

    // Update an action
    override suspend fun update( id: Int, obj: Any ) = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(
            SchemaUtils.updateQuery(TABLE, listColumns, COLUMN_ID)
        )
        obj as Action
        statement.setInt(0, id)
        statement.setString(1, obj.nameAction)
        statement.setString(2, obj.descriptionAction)
        statement.setString(3, SchemaUtils.getCurrentDate())
        statement.executeUpdate()
    }

    // Delete an action
    override suspend fun delete(id: Int) = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement("DELETE FROM $TABLE WHERE $COLUMN_ID = ?;")
        statement.setInt(1, id)
        statement.executeUpdate()
    }

    // List all actions
    override suspend fun list(): List<Action> = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement( "SELECT * FROM $TABLE" )
        val resultSet = statement.executeQuery()

        val actionList = mutableListOf<Action>()

        while (resultSet.next()) {

            val idAction = resultSet.getInt( COLUMN_ID )
            val nameAction = resultSet.getString( COLUMN_NAME )
            val valueAction = resultSet.getString( COLUMN_DESCRIPTION )
            val dateAction = resultSet.getString( COLUMN_DATE )

            actionList.add(
                Action(
                    idAction,
                    nameAction,
                    valueAction,
                    dateAction
                )
            )
        }

        if (actionList.isNotEmpty()) {
            return@withContext actionList
        } else {
            throw Exception(SchemaUtils.RECORD_NOT_FOUND)
        }
    }
}