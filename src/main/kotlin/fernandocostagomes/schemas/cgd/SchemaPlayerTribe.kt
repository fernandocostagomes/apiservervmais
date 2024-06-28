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
data class PlayerTribe(
    val id: Long = 0L,
    var idPlayer: Long,
    var idTribe: Long,
    var insertData: String,
    var permission: List<Long>,
    var data: String)
    class ServicePlayerTribe(private val connection: Connection): SchemaInterface {

        companion object {
            private const val TABLE = "v_cgd_playerTribe"
            private const val COLUMN_ID = "v_cgd_playerTribe_id"
            private const val COLUMN_ID_PLAYER = "v_cgd_playerTribe_id_player"
            private const val COLUMN_ID_TRIBE = "v_cgd_playerTribe_id_tribe"
            private const val COLUMN_INSERT_DATA = "v_cgd_playerTribe_insertData"
            private const val COLUMN_PERMISSION = "v_cgd_playerTribe_permission"
            private const val COLUMN_DATE = "v_cgd_playerTribe_date"

            private const val COLUMN_ID_QUERY = "$COLUMN_ID SERIAL PRIMARY KEY, "
            private const val COLUMN_ID_PLAYER_QUERY = "$COLUMN_ID_PLAYER INTEGER NOT NULL, "
            private const val COLUMN_ID_TRIBE_QUERY = "$COLUMN_ID_TRIBE INTEGER NOT NULL, "
            private const val COLUMN_INSERT_DATA_QUERY = "$COLUMN_INSERT_DATA VARCHAR(20), "
            private const val COLUMN_PERMISSION_QUERY = "$COLUMN_PERMISSION VARCHAR(20), "
            private const val COLUMN_DATE_QUERY = "$COLUMN_DATE VARCHAR(20)"

            val listColumns = listOf(
                COLUMN_ID,
                COLUMN_ID_PLAYER,
                COLUMN_ID_TRIBE,
                COLUMN_INSERT_DATA,
                COLUMN_PERMISSION,
                COLUMN_DATE
            )

            val listColumnsQuery = listOf(
                COLUMN_ID_QUERY,
                COLUMN_ID_PLAYER_QUERY,
                COLUMN_ID_TRIBE_QUERY,
                COLUMN_INSERT_DATA_QUERY,
                COLUMN_PERMISSION_QUERY,
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

        private fun getResultset(pResultSet: ResultSet): PlayerTribe {
            return PlayerTribe(
                pResultSet.getLong( COLUMN_ID ),
                pResultSet.getLong( COLUMN_ID_PLAYER ),
                pResultSet.getLong( COLUMN_ID_TRIBE ),
                pResultSet.getString( COLUMN_INSERT_DATA ),
                pResultSet.getString( COLUMN_PERMISSION ).split(",").map { it.toLong() },
                pResultSet.getString( COLUMN_DATE ))
        }

        private fun getStatement(pStatement: PreparedStatement, pObj: Any): PreparedStatement {
            pObj as PlayerTribe
            pStatement.setLong(1, pObj.idPlayer)
            pStatement.setLong(2, pObj.idTribe)
            pStatement.setString(3, pObj.insertData)
            pStatement.setString(4, pObj.permission.joinToString(","))
            pStatement.setString(5, SchemaUtils.getCurrentDate())
            return pStatement
        }
        // Create new playerTribe
        override suspend fun create( obj: Any ): Int = withContext( Dispatchers.IO ) {

            val statement = connection.prepareStatement(
                SchemaUtils.insertQuery(TABLE, listColumns),
                Statement.RETURN_GENERATED_KEYS)

            obj as PlayerTribe

            val statementPos: PreparedStatement = getStatement( statement, obj )
            statementPos.executeUpdate()

            val generatedKeys = statementPos.generatedKeys

            if (generatedKeys.next()) {
                return@withContext generatedKeys.getInt(1)
            } else {
                throw Exception(SchemaUtils.UNABLE_NEW_ID_INSERTED)
            }
        }

        // Read a playerTribe
        override suspend fun read(id: Int): PlayerTribe = withContext( Dispatchers.IO ) {
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

        // Update a playerTribe
        override suspend fun update( id: Int, obj: Any ) = withContext( Dispatchers.IO ) {
            val statement = connection.prepareStatement(
                SchemaUtils.updateQuery(
                    TABLE,
                    listColumns,
                    COLUMN_ID
                )
            )

            obj as PlayerTribe

            val statementPos: PreparedStatement = getStatement( statement, obj )
            statementPos.setInt(0, id)
            statementPos.executeUpdate()
        }

        // Delete a playerTribe
        override suspend fun delete(id: Int) = withContext( Dispatchers.IO ) {
            val statement = connection.prepareStatement("DELETE FROM $TABLE WHERE $COLUMN_ID = ?;")
            statement.setInt(1, id)
            statement.executeUpdate()
        }

        // List all playerTribe
        override suspend fun list(): List<PlayerTribe> = withContext( Dispatchers.IO ) {

            val statement = connection.prepareStatement( "SELECT * FROM $TABLE" )

            val resultSet = statement.executeQuery()

            val playerTribeMutableList = mutableListOf<PlayerTribe>()

            while (resultSet.next()) {
                playerTribeMutableList.add( getResultset( resultSet ) )
            }

            if (playerTribeMutableList.isNotEmpty()) {
                return@withContext playerTribeMutableList
            } else {
                throw Exception(SchemaUtils.RECORD_NOT_FOUND)
            }
        }

    }