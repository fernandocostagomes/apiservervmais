package fernandocostagomes.schemas

import io.swagger.v3.oas.annotations.parameters.RequestBody
import kotlinx.coroutines.*
import kotlinx.serialization.Serializable
import java.sql.Connection
import java.sql.SQLException
import java.sql.Statement

@Serializable
data class Action(    
    val nameAction: String,
    val descriptionAction: String)
class ServiceAction(private val connection: Connection): SchemaInterface {
    companion object {
        private const val TABLE = "v_action"
        private const val COLUMN_ID = "v_action_id"        
        private const val COLUMN_NAME = "v_action_name"
        private const val COLUMN_DESCRIPTION = "v_action_description"

        private const val CREATE_TABLE_ACTION =
                "CREATE TABLE IF NOT EXISTS " +
                        "$TABLE (" +
                        "$COLUMN_ID SERIAL PRIMARY KEY, " +                        
                        "$COLUMN_NAME VARCHAR(20), " +
                        "$COLUMN_DESCRIPTION VARCHAR(30));"

        private const val SELECT_ACTION_BY_ID = "SELECT " +
                "$COLUMN_NAME, " +
                "$COLUMN_DESCRIPTION FROM $TABLE WHERE $COLUMN_ID = ?;"

        private const val INSERT_ACTION = "INSERT INTO " +
                "$TABLE (" +
                "$COLUMN_NAME, " +
                "$COLUMN_DESCRIPTION) VALUES (?, ?);"

        private const val UPDATE_ACTION = "UPDATE " +
                "$TABLE SET " +
                "$COLUMN_NAME = ?, " +
                "$COLUMN_DESCRIPTION = ? " +
                "WHERE $COLUMN_ID = ?;"

        private const val DELETE_ACTION = "DELETE FROM $TABLE WHERE $COLUMN_ID = ?;"

        private const val LIST_ACTION = "SELECT * FROM $TABLE}"
    }

    init {
        try {
            val statement = connection.createStatement()
            statement.executeUpdate(CREATE_TABLE_ACTION)
        } catch (e: SQLException) {
            println(e.toString())
        }
    }

    // Create new action
    override suspend fun create( obj: Any ): Int = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(INSERT_ACTION, Statement.RETURN_GENERATED_KEYS)
        obj as Action
        statement.setString(1, obj.nameAction)
        statement.setString(2, obj.descriptionAction)
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
        val statement = connection.prepareStatement(SELECT_ACTION_BY_ID)
        statement.setInt(1, id)
        val resultSet = statement.executeQuery()

        if (resultSet.next()) {
            val name = resultSet.getString(COLUMN_NAME)
            val description = resultSet.getString(COLUMN_DESCRIPTION)
            return@withContext Action( name, description )
        } else {
            throw Exception("Record not found")
        }
    }

    // Update an action
    override suspend fun update( id: Int, obj: Any ) = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(UPDATE_ACTION)
        obj as Action
        statement.setInt(0, id)
        statement.setString(2, obj.nameAction)
        statement.setString(3, obj.descriptionAction)
        statement.executeUpdate()
    }

    // Delete an action
    override suspend fun delete(id: Int) = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(DELETE_ACTION)
        statement.setInt(1, id)
        statement.executeUpdate()
    }

    // List all actions
    override suspend fun list(): List<Action> = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement( LIST_ACTION )
        val resultSet = statement.executeQuery()

        val actionList = mutableListOf<Action>()

        while (resultSet.next()) {

            val nameAction = resultSet.getString( COLUMN_NAME )
            val valueAction = resultSet.getString( COLUMN_DESCRIPTION )

            val action = Action( nameAction, valueAction)
            actionList.add( action )
        }

        if (actionList.isNotEmpty()) {
            return@withContext actionList
        } else {
            throw Exception("No records found")
        }
    }
}