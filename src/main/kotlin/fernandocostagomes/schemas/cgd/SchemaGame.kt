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
data class Game(
    val id: Long = 0L,
    var idtribe: Long,
    var idwinpri: Long,
    var idwinsec: Long,
    var idlosepri: Long,
    var idlosesec: Long,
    var data: String )
    class ServiceGame(private val connection: Connection): SchemaInterface {

        companion object {
            private const val TABLE = "v_cgd_game"
            private const val COLUMN_ID = "v_cgd_game_id"
            private const val COLUMN_ID_TRIBE = "v_cgd_game_tribe_id"
            private const val COLUMN_WIN_PRI = "v_cgd_game_win_pri"
            private const val COLUMN_WIN_SEC = "v_cgd_game_win_sec"
            private const val COLUMN_LOSE_PRI = "v_cgd_game_lose_pri"
            private const val COLUMN_LOSE_SEC = "v_cgd_game_lose_sec"
            private const val COLUMN_DATE = "v_cgd_game_date"

            private const val COLUMN_ID_QUERY = "$COLUMN_ID SERIAL PRIMARY KEY, "
            private const val COLUMN_ID_TRIBE_QUERY = "$COLUMN_ID_TRIBE INTEGER NOT NULL, "
            private const val COLUMN_WIN_PRI_QUERY = "$COLUMN_WIN_PRI INTEGER NOT NULL, "
            private const val COLUMN_WIN_SEC_QUERY = "$COLUMN_WIN_SEC INTEGER NOT NULL, "
            private const val COLUMN_LOSE_PRI_QUERY = "$COLUMN_LOSE_PRI INTEGER NOT NULL, "
            private const val COLUMN_LOSE_SEC_QUERY = "$COLUMN_LOSE_SEC INTEGER NOT NULL, "
            private const val COLUMN_DATE_QUERY = "$COLUMN_DATE VARCHAR(20)"

            val listColumns = listOf(
                COLUMN_ID,
                COLUMN_ID_TRIBE,
                COLUMN_WIN_PRI,
                COLUMN_WIN_SEC,
                COLUMN_LOSE_PRI,
                COLUMN_LOSE_SEC,
                COLUMN_DATE
            )

            val listColumnsQuery = listOf(
                COLUMN_ID_QUERY,
                COLUMN_ID_TRIBE_QUERY,
                COLUMN_WIN_PRI_QUERY,
                COLUMN_WIN_SEC_QUERY,
                COLUMN_LOSE_PRI_QUERY,
                COLUMN_LOSE_SEC_QUERY,
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

        private fun getResultset(pResultSet: ResultSet): Game {
            return Game(
                pResultSet.getLong( COLUMN_ID ),
                pResultSet.getLong( COLUMN_ID_TRIBE ),
                pResultSet.getLong( COLUMN_WIN_PRI ),
                pResultSet.getLong( COLUMN_WIN_SEC ),
                pResultSet.getLong( COLUMN_LOSE_PRI ),
                pResultSet.getLong( COLUMN_LOSE_SEC ),
                pResultSet.getString( COLUMN_DATE )
            )
        }

        private fun getStatement(pStatement: PreparedStatement, pObj: Any): PreparedStatement {
            pObj as Game
            pStatement.setLong(1, pObj.idtribe)
            pStatement.setLong(2, pObj.idwinpri)
            pStatement.setLong(3, pObj.idwinsec)
            pStatement.setLong(4, pObj.idlosepri)
            pStatement.setLong(5, pObj.idlosesec)
            pStatement.setString(6, SchemaUtils.getCurrentDate())
            return pStatement
        }
        // Create new game
        override suspend fun create( obj: Any ): Int = withContext( Dispatchers.IO ) {

            val statement = connection.prepareStatement(
                SchemaUtils.insertQuery(TABLE, listColumns),
                Statement.RETURN_GENERATED_KEYS
            )

            obj as Game

            val statementPos: PreparedStatement = getStatement( statement, obj )
            statementPos.executeUpdate()

            val generatedKeys = statementPos.generatedKeys

            if (generatedKeys.next()) {
                return@withContext generatedKeys.getInt(1)
            } else {
                throw Exception(SchemaUtils.UNABLE_NEW_ID_INSERTED)
            }
        }

        // Read an game
        override suspend fun read(id: Int): Game = withContext( Dispatchers.IO ) {
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

        // Update an game
        override suspend fun update( id: Int, obj: Any ) = withContext( Dispatchers.IO ) {
            val statement = connection.prepareStatement(
                SchemaUtils.updateQuery(
                    TABLE,
                    listColumns,
                    COLUMN_ID
                )
            )

            obj as Game

            val statementPos: PreparedStatement = getStatement( statement, obj )
            statementPos.setInt(0, id)
            statementPos.executeUpdate()
        }

        // Delete an game
        override suspend fun delete(id: Int) = withContext( Dispatchers.IO ) {
            val statement = connection.prepareStatement("DELETE FROM $TABLE WHERE $COLUMN_ID = ?;")
            statement.setInt(1, id)
            statement.executeUpdate()
        }

        // List all game
        override suspend fun list(): List<Game> = withContext( Dispatchers.IO ) {

            val statement = connection.prepareStatement( "SELECT * FROM $TABLE" )

            val resultSet = statement.executeQuery()

            val gameMutableList = mutableListOf<Game>()

            while (resultSet.next()) {
                gameMutableList.add( getResultset( resultSet ) )
            }

            if (gameMutableList.isNotEmpty()) {
                return@withContext gameMutableList
            } else {
                throw Exception(SchemaUtils.RECORD_NOT_FOUND)
            }
        }

    }