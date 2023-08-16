package fernandocostagomes.schemas

import fernandocostagomes.schemas.SchemaUtils.Companion.createTable
import fernandocostagomes.schemas.SchemaUtils.Companion.insertQuery
import fernandocostagomes.schemas.SchemaUtils.Companion.selectQuery
import fernandocostagomes.schemas.SchemaUtils.Companion.updateQuery
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import org.junit.Test
import java.sql.Connection
import java.sql.PreparedStatement

class SchemaUtilsTest {
    private val mListTables: List<String> = listOf("role", "permission", "action")
    private val mockConnection = mockk<Connection>()
    private val mockStatement = mockk<PreparedStatement>(relaxed = true)

    @Test
    fun createTableQueryTest(){
        for( table in mListTables ){
            val columnNames = when(table) {
                "role" -> ServiceRole.listColumnsQuery
                "permission" -> ServicePermission.listColumnsQuery
                "action" -> ServiceAction.listColumnsQuery
                else -> listOf()
            }

            val queryExpected = when(table) {
                "role" -> "CREATE TABLE IF NOT EXISTS role ( " +
                        "v_role_id SERIAL PRIMARY KEY, " +
                        "v_role_name VARCHAR(20), " +
                        "v_role_description VARCHAR(30) );"
                "permission" -> "CREATE TABLE IF NOT EXISTS permission ( " +
                        "v_permission_id SERIAL PRIMARY KEY, " +
                        "v_permission_name VARCHAR(20), " +
                        "v_permission_description VARCHAR(30), " +
                        "v_permission_date VARCHAR(20), " +
                        "v_role_id INTEGER NOT NULL, " +
                        "v_action_id INTEGER NOT NULL );"
                "action" -> "CREATE TABLE IF NOT EXISTS action ( " +
                        "v_action_id SERIAL PRIMARY KEY, " +
                        "v_action_name VARCHAR(20), " +
                        "v_action_description VARCHAR(30) );"
                else -> ""
            }
            testCreateTableQuery(table, columnNames, queryExpected)
        }
    }

    @Test
    fun insertQueryTest() {
        for(table in mListTables) {
            val columnNames = when(table) {
                "role" -> ServiceRole.listColumns
                "permission" -> ServicePermission.listColumns
                "action" -> ServiceAction.listColumns
                else -> listOf()
            }

            val queryExpected = when(table) {
                "role" -> "INSERT INTO role ( `v_role_name`, `v_role_description` ) VALUES ( ?, ? );"
                "permission" -> "INSERT INTO permission ( `v_permission_name`, `v_permission_description`, " +
                        "`v_permission_date`, `v_role_id`, `v_action_id` ) VALUES ( ?, ?, ?, ?, ? );"
                "action" -> "INSERT INTO action ( `v_action_name`, `v_action_description` ) VALUES ( ?, ? );"
                else -> ""
            }
            testInsertQuery(table, columnNames, queryExpected)
        }
    }

    @Test
    fun selectQueryTest(){
        for (table in mListTables){
            val columnNames = when(table) {
                "role" -> ServiceRole.listColumns
                "permission" -> ServicePermission.listColumns
                "action" -> ServiceAction.listColumns
                else -> listOf()
            }

            val queryExpected = when(table) {
                "role" -> "SELECT `v_role_id`, `v_role_name`, `v_role_description` FROM role WHERE `v_role_id` = ?;"
                "permission" -> "SELECT `v_permission_id`, `v_permission_name`, `v_permission_description`, " +
                        "`v_permission_date`, `v_role_id`, `v_action_id` FROM permission WHERE `v_permission_id` = ?;"
                "action" -> "SELECT `v_action_id`, `v_action_name`, `v_action_description` FROM action WHERE `v_action_id` = ?;"
                else -> ""
            }
            testSelectQuery(table, columnNames, "v_${table}_id", queryExpected)
        }
    }

    @Test
    fun updateQueryTest() {
        for( table in mListTables ){
            val columnNames = when(table) {
                "role" -> ServiceRole.listColumns
                "permission" -> ServicePermission.listColumns
                "action" -> ServiceAction.listColumns
                else -> listOf()
            }

            val queryExpected = when(table) {
                "role" -> "UPDATE role SET `v_role_name` = ?, `v_role_description` = ? WHERE `v_role_id` = ?;"
                "permission" -> "UPDATE permission SET `v_permission_name` = ?, `v_permission_description` = ?, " +
                        "`v_permission_date` = ?, `v_role_id` = ?, `v_action_id` = ? WHERE `v_permission_id` = ?;"
                "action" -> "UPDATE action SET `v_action_name` = ?, `v_action_description` = ? WHERE `v_action_id` = ?;"
                else -> ""
            }
            testUpdateQuery(table, columnNames, "v_${table}_id", queryExpected)
        }
    }

    private fun testUpdateQuery(
        tableName: String,
        columnNames: List<String>,
        primaryKeyColumn: String,
        expectedQuery: String
    ) {
        val actualQuery = updateQuery(tableName, columnNames, primaryKeyColumn)
        assertEquals(expectedQuery, actualQuery)
    }

    private fun testInsertQuery(
        tableName: String,
        columnNames: List<String>,
        columnNamesExpectation: String
    ) {
        every { mockConnection.createStatement() } returns mockStatement
        val actualQuery = insertQuery(tableName, columnNames)
        return assertEquals(columnNamesExpectation, actualQuery)
    }

    private fun testSelectQuery(
        tableName: String,
        columnNames: List<String>,
        primaryKeyColumn: String,
        columnNamesExpectation: String
    ) {
        every { mockConnection.createStatement() } returns mockStatement
        val actualQuery = selectQuery( tableName, primaryKeyColumn, columnNames)
        return assertEquals(columnNamesExpectation, actualQuery)
    }

    private  fun testCreateTableQuery(
        tableName: String,
        columnNames: List<String>,
        columnNamesExpectation: String
    ){
        every { mockConnection.createStatement() } returns mockStatement
        val actualQuery = createTable( tableName, columnNames )
        return assertEquals(columnNamesExpectation, actualQuery)
    }
}