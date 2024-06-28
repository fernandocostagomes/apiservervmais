package fernandocostagomes.schemas.cgd

import fernandocostagomes.schemas.SchemaInterface
import fernandocostagomes.schemas.SchemaUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import java.sql.*

/**
 * Criado por fernando.gomes em 25/06/2024.
 */
@Serializable
data class Player(
    val id: Long = 0L,
    var name: String,
    var pwd: String,
    var phone: String,
    var email: String,
    var data: String )
    class ServicePlayer(private val connection: Connection): SchemaInterface {

        companion object {
            private const val TABLE = "v_cgd_player"
            private const val COLUMN_ID = "v_cgd_player_id"
            private const val COLUMN_NAME = "v_cgd_player_name"
            private const val COLUMN_PWD = "v_cgd_player_pwd"
            private const val COLUMN_PHONE = "v_cgd_player_phone"
            private const val COLUMN_EMAIL = "v_cgd_player_email"
            private const val COLUMN_DATE = "v_cgd_player_date"

            private const val COLUMN_ID_QUERY = "$COLUMN_ID SERIAL PRIMARY KEY, "
            private const val COLUMN_NAME_QUERY = "$COLUMN_NAME VARCHAR(20), "
            private const val COLUMN_PWD_QUERY = "$COLUMN_PWD VARCHAR(20), "
            private const val COLUMN_PHONE_QUERY = "$COLUMN_PHONE VARCHAR(20), "
            private const val COLUMN_EMAIL_QUERY = "$COLUMN_EMAIL VARCHAR(40), "
            private const val COLUMN_DATE_QUERY = "$COLUMN_DATE VARCHAR(20)"

            val listColumns = listOf(
                COLUMN_ID,
                COLUMN_NAME,
                COLUMN_PWD,
                COLUMN_PHONE,
                COLUMN_EMAIL,
                COLUMN_DATE
            )

            val listColumnsQuery = listOf(
                COLUMN_ID_QUERY,
                COLUMN_NAME_QUERY,
                COLUMN_PWD_QUERY,
                COLUMN_PHONE_QUERY,
                COLUMN_EMAIL_QUERY,
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

        private fun getResultset(pResultSet: ResultSet): Player {
            return Player(
                pResultSet.getLong( COLUMN_ID ),
                pResultSet.getString( COLUMN_NAME ),
                pResultSet.getString( COLUMN_PWD ),
                pResultSet.getString( COLUMN_PHONE ),
                pResultSet.getString( COLUMN_EMAIL ),
                pResultSet.getString( COLUMN_DATE )
            )
        }

        private fun getStatement(pStatement: PreparedStatement, pObj: Any): PreparedStatement {
            pObj as Player
            pStatement.setString(1, pObj.name)
            pStatement.setString(2, pObj.pwd)
            pStatement.setString(3, pObj.phone)
            pStatement.setString(4, pObj.email)
            pStatement.setString(5, SchemaUtils.getCurrentDate())
            return pStatement
        }
        // Create new player
        override suspend fun create( obj: Any ): Int = withContext( Dispatchers.IO ) {

            val statement = connection.prepareStatement(
                SchemaUtils.insertQuery(ServicePlayer.TABLE, ServicePlayer.listColumns),
                Statement.RETURN_GENERATED_KEYS
            )

            obj as Player

            val statementPos: PreparedStatement = getStatement( statement, obj )
            statementPos.executeUpdate()

            val generatedKeys = statementPos.generatedKeys

            if (generatedKeys.next()) {
                return@withContext generatedKeys.getInt(1)
            } else {
                throw Exception(SchemaUtils.UNABLE_NEW_ID_INSERTED)
            }
        }

        // Read an player
        override suspend fun read(id: Int): Player = withContext( Dispatchers.IO ) {
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

        // Update an player
        override suspend fun update( id: Int, obj: Any ) = withContext( Dispatchers.IO ) {
            val statement = connection.prepareStatement(
                SchemaUtils.updateQuery(
                    TABLE,
                    listColumns,
                    COLUMN_ID
                )
            )

            obj as Player

            val statementPos: PreparedStatement = getStatement( statement, obj )
            statementPos.setInt(0, id)
            statementPos.executeUpdate()
        }

        // Delete an player
        override suspend fun delete(id: Int) = withContext( Dispatchers.IO ) {
            val statement = connection.prepareStatement("DELETE FROM $TABLE WHERE $COLUMN_ID = ?;")
            statement.setInt(1, id)
            statement.executeUpdate()
        }

        // List all player
        override suspend fun list(): List<Player> = withContext( Dispatchers.IO ) {

            val statement = connection.prepareStatement( "SELECT * FROM $TABLE" )

            val resultSet = statement.executeQuery()

            val playerMutableList = mutableListOf<Player>()

            while (resultSet.next()) {
                playerMutableList.add( getResultset( resultSet ) )
            }

            if (playerMutableList.isNotEmpty()) {
                return@withContext playerMutableList
            } else {
                throw Exception(SchemaUtils.RECORD_NOT_FOUND)
            }
        }

    }