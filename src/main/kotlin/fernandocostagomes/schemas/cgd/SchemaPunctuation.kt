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
data class Punctuation(
    val id: Long = 0L,
    var name: String,
    var value: Long,
    var data: String )
    class ServicePunctuation(private val connection: Connection): SchemaInterface {

        companion object {
            private const val TABLE = "v_cgd_punctuation"
            private const val COLUMN_ID = "v_cgd_punctuation_id"
            private const val COLUMN_NAME = "v_cgd_punctuation_name"
            private const val COLUMN_VALUE = "v_cgd_punctuation_value"
            private const val COLUMN_DATE = "v_cgd_punctuation_date"

            private const val COLUMN_ID_QUERY = "$COLUMN_ID SERIAL PRIMARY KEY, "
            private const val COLUMN_NAME_QUERY = "$COLUMN_NAME VARCHAR(50), "
            private const val COLUMN_VALUE_QUERY = "$COLUMN_VALUE BIGINT, "
            private const val COLUMN_DATE_QUERY = "$COLUMN_DATE VARCHAR(20)"

            val listColumns = listOf(
                COLUMN_ID,
                COLUMN_NAME,
                COLUMN_VALUE,
                COLUMN_DATE
            )

            val listColumnsQuery = listOf(
                COLUMN_ID_QUERY,
                COLUMN_NAME_QUERY,
                COLUMN_VALUE_QUERY,
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

        private fun getResultset(pResultSet: ResultSet): Punctuation {
            return Punctuation(
                pResultSet.getLong( COLUMN_ID ),
                pResultSet.getString( COLUMN_NAME ),
                pResultSet.getLong( COLUMN_VALUE ),
                pResultSet.getString( COLUMN_DATE )
            )
        }

        private fun getStatement(pStatement: PreparedStatement, pObj: Any): PreparedStatement {
            pObj as Punctuation
            pStatement.setString(1, pObj.name)
            pStatement.setLong(2, pObj.value)
            pStatement.setString(3, pObj.data)
            pStatement.setString(4, SchemaUtils.getCurrentDate())
            return pStatement
        }
        // Create new punctuation
        override suspend fun create( obj: Any ): Int = withContext( Dispatchers.IO ) {

            val statement = connection.prepareStatement(
                SchemaUtils.insertQuery(TABLE, listColumns),
                Statement.RETURN_GENERATED_KEYS
            )

            obj as Punctuation

            val statementPos: PreparedStatement = getStatement( statement, obj )
            statementPos.executeUpdate()

            val generatedKeys = statementPos.generatedKeys

            if (generatedKeys.next()) {
                return@withContext generatedKeys.getInt(1)
            } else {
                throw Exception(SchemaUtils.UNABLE_NEW_ID_INSERTED)
            }
        }

        // Read an punctuation
        override suspend fun read(id: Int): Punctuation = withContext( Dispatchers.IO ) {
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

        // Update an punctuation
        override suspend fun update( id: Int, obj: Any ) = withContext( Dispatchers.IO ) {
            val statement = connection.prepareStatement(
                SchemaUtils.updateQuery(
                    TABLE,
                    listColumns,
                    COLUMN_ID
                )
            )

            obj as Punctuation

            val statementPos: PreparedStatement = getStatement( statement, obj )
            statementPos.setInt(0, id)
            statementPos.executeUpdate()
        }

        // Delete an punctuation
        override suspend fun delete(id: Int) = withContext( Dispatchers.IO ) {
            val statement = connection.prepareStatement("DELETE FROM $TABLE WHERE $COLUMN_ID = ?;")
            statement.setInt(1, id)
            statement.executeUpdate()
        }

        // List all punctuation
        override suspend fun list(): List<Punctuation> = withContext( Dispatchers.IO ) {

            val statement = connection.prepareStatement( "SELECT * FROM $TABLE" )

            val resultSet = statement.executeQuery()

            val punctuationMutableList = mutableListOf<Punctuation>()

            while (resultSet.next()) {
                punctuationMutableList.add( getResultset( resultSet ) )
            }

            if (punctuationMutableList.isNotEmpty()) {
                return@withContext punctuationMutableList
            } else {
                throw Exception(SchemaUtils.RECORD_NOT_FOUND)
            }
        }

    }