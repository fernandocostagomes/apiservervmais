package fernandocostagomes.schemas

class SchemaUtils {
    companion object {

        const val RECORD_NOT_FOUND = "Record not found"
        const val UNABLE_NEW_ID_INSERTED = "Unable to retrieve the id of the newly inserted role"

        fun createTable(tableName: String, columnNames: List<String>): String {
            val columns = columnNames.joinToString("") { columnName ->
                columnName
            }

            return "CREATE TABLE IF NOT EXISTS $tableName ( $columns );"
        }

        fun selectQuery(tableName: String, primaryKeyColumn: String, columnNames: List<String>): String {
            val columns = columnNames.joinToString(", ") { columnName ->
                "`$columnName`"
            }

            return "SELECT $columns FROM $tableName WHERE `$primaryKeyColumn` = ?;"
        }

        fun insertQuery( tableName: String, columnNames: List<String> ): String {
            val tableId = tableName + "_" + "id"
            val columns = columnNames.filter { !it.contains( tableId ) }.joinToString(", ") { columnName ->
                "`$columnName`"
            }

            val values = columnNames.filter { !it.contains( tableId ) }.joinToString(", ") {
                "?"
            }
            return "INSERT INTO $tableName ( $columns ) VALUES ( $values );"
        }

        fun updateQuery(tableName: String, columnNames: List<String>, primaryKeyColumn: String): String {
            val setClause = columnNames.filter { !it.contains( tableName + "_" + "id" ) }
                .joinToString(", ") { columnName ->
                "`$columnName` = ?"
            }

            return "UPDATE $tableName SET $setClause WHERE `$primaryKeyColumn` = ?;"
        }
    }
}