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
    val descriptionAction: String)
class ServiceAction(private val connection: Connection): SchemaInterface {
    companion object {
        private const val TABLE = "v_action"
        private const val COLUMN_ID = "v_action_id"
        private const val COLUMN_NAME = "v_action_name"
        private const val COLUMN_DESCRIPTION = "v_action_description"

        private const val COLUMN_ID_QUERY = "$COLUMN_ID SERIAL PRIMARY KEY, "
        private const val COLUMN_NAME_QUERY = "$COLUMN_NAME VARCHAR(20), "
        private const val COLUMN_DESCRIPTION_QUERY = "$COLUMN_DESCRIPTION VARCHAR(30)"

        val listColumns = listOf(
            COLUMN_ID,
            COLUMN_NAME,
            COLUMN_DESCRIPTION
        )

        val listColumnsQuery = listOf(
            COLUMN_ID_QUERY,
            COLUMN_NAME_QUERY,
            COLUMN_DESCRIPTION_QUERY
        )
    }

    init {
        try {
            val statement = connection.createStatement()
            statement.executeUpdate(SchemaUtils.createTable( TABLE, listColumnsQuery ))
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
        print("SchemaAction: $statement")
        statement.executeUpdate()

        val generatedKeys = statement.generatedKeys
        if (generatedKeys.next()) {
            return@withContext generatedKeys.getInt(1)
        } else {
            throw Exception("Unable to retrieve the id of the newly inserted action")
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
            return@withContext Action(idAction, nameAction, descriptionAction )
        } else {
            throw Exception("Record not found")
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

            val action = Action( idAction, nameAction, valueAction )
            actionList.add( action )
        }

        if (actionList.isNotEmpty()) {
            return@withContext actionList
        } else {
            throw Exception("No records found")
        }
    }
}