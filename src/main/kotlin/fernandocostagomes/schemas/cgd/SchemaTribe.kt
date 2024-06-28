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
data class Tribe(
    val id: Long = 0L,
    var name: String,
    var pwd: String,
    var punctuation: List<Long>,
    var data: String,
    var idPlayer: Long)
    class ServiceTribe(private val connection: Connection): SchemaInterface {

        companion object {
            private const val TABLE = "v_cgd_tribe"
            private const val COLUMN_ID = "v_cgd_tribe_id"
            private const val COLUMN_NAME = "v_cgd_tribe_name"
            private const val COLUMN_PWD = "v_cgd_tribe_pwd"
            private const val COLUMN_PUNCTUATION = "v_cgd_tribe_punctuation"
            private const val COLUMN_DATE = "v_cgd_tribe_date"
            private const val COLUMN_ID_PLAYER = "v_cgd_tribe_id_player"

            private const val COLUMN_ID_QUERY = "$COLUMN_ID SERIAL PRIMARY KEY, "
            private const val COLUMN_NAME_QUERY = "$COLUMN_NAME VARCHAR(20), "
            private const val COLUMN_PWD_QUERY = "$COLUMN_PWD VARCHAR(20), "
            private const val COLUMN_PUNCTUATION_QUERY = "$COLUMN_PUNCTUATION VARCHAR(20), "
            private const val COLUMN_DATE_QUERY = "$COLUMN_DATE VARCHAR(20)"
            private const val COLUMN_ID_PLAYER_QUERY = "$COLUMN_ID_PLAYER INTEGER NOT NULL "

            val listColumns = listOf(
                COLUMN_ID,
                COLUMN_NAME,
                COLUMN_PWD,
                COLUMN_PUNCTUATION,
                COLUMN_DATE,
                COLUMN_ID_PLAYER
            )

            val listColumnsQuery = listOf(
                COLUMN_ID_QUERY,
                COLUMN_NAME_QUERY,
                COLUMN_PWD_QUERY,
                COLUMN_PUNCTUATION_QUERY,
                COLUMN_DATE_QUERY,
                COLUMN_ID_PLAYER_QUERY
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

        private fun getResultset(pResultSet: ResultSet): Tribe {
            return Tribe(
                pResultSet.getLong( COLUMN_ID ),
                pResultSet.getString( COLUMN_NAME ),
                pResultSet.getString( COLUMN_PWD ),
                pResultSet.getString( COLUMN_PUNCTUATION ).split(",").map { it.toLong() },
                pResultSet.getString( COLUMN_DATE ),
                pResultSet.getLong( COLUMN_ID_PLAYER )
            )
        }

        private fun getStatement(pStatement: PreparedStatement, pObj: Any): PreparedStatement {
            pObj as Tribe
            pStatement.setString(1, pObj.name)
            pStatement.setString(2, pObj.pwd)
            pStatement.setString(3, pObj.punctuation.joinToString(","))
            pStatement.setString(4, SchemaUtils.getCurrentDate())
            pStatement.setLong(5, pObj.idPlayer)
            return pStatement
        }
        // Create new tribe
        override suspend fun create( obj: Any ): Int = withContext( Dispatchers.IO ) {

            val statement = connection.prepareStatement(
                SchemaUtils.insertQuery(TABLE, listColumns),
                Statement.RETURN_GENERATED_KEYS
            )

            obj as Tribe

            val statementPos: PreparedStatement = getStatement( statement, obj )
            statementPos.executeUpdate()

            val generatedKeys = statementPos.generatedKeys

            if (generatedKeys.next()) {
                return@withContext generatedKeys.getInt(1)
            } else {
                throw Exception(SchemaUtils.UNABLE_NEW_ID_INSERTED)
            }
        }

        // Read a tribe
        override suspend fun read(id: Int): Tribe = withContext( Dispatchers.IO ) {
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

        // Update a tribe
        override suspend fun update( id: Int, obj: Any ) = withContext( Dispatchers.IO ) {
            val statement = connection.prepareStatement(
                SchemaUtils.updateQuery(
                    TABLE,
                    listColumns,
                    COLUMN_ID
                )
            )

            obj as Tribe

            val statementPos: PreparedStatement = getStatement( statement, obj )
            statementPos.setInt(0, id)
            statementPos.executeUpdate()
        }

        // Delete a tribe
        override suspend fun delete(id: Int) = withContext( Dispatchers.IO ) {
            val statement = connection.prepareStatement("DELETE FROM $TABLE WHERE $COLUMN_ID = ?;")
            statement.setInt(1, id)
            statement.executeUpdate()
        }

        // List all tribe
        override suspend fun list(): List<Tribe> = withContext( Dispatchers.IO ) {

            val statement = connection.prepareStatement( "SELECT * FROM $TABLE" )

            val resultSet = statement.executeQuery()

            val tribeMutableList = mutableListOf<Tribe>()

            while (resultSet.next()) {
                tribeMutableList.add( getResultset( resultSet ) )
            }

            if (tribeMutableList.isNotEmpty()) {
                return@withContext tribeMutableList
            } else {
                throw Exception(SchemaUtils.RECORD_NOT_FOUND)
            }
        }

    }