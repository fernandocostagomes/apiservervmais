package fernandocostagomes.models

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

            return "SELECT $columns FROM $tableName WHERE $primaryKeyColumn = ?;"
        }

        fun insertQuery( tableName: String, columnNames: List<String> ): String {
            val columns = columnNames.joinToString(", ") { columnName ->
                "`$columnName`"
            }

            var placeholders = ""

            for ( item in columnNames ) {
                if(!item.contains("id"))
                    placeholders += "?, "
            }

            return "INSERT INTO $tableName ($columns) VALUES ($placeholders);"
        }

        fun updateQuery(tableName: String, columnNames: List<String>, primaryKeyColumn: String): String {
            val setClause = columnNames.joinToString(", ") { columnName ->
                "`$columnName` = ?"
            }

            val placeholders = getPlaceholders(columnNames, "?;")

            return "UPDATE $tableName SET $setClause WHERE $primaryKeyColumn = $placeholders;"
        }

        fun getPlaceholders(columnNames: List<String>, lastPlaceholder: String = "?"): String {
            val placeholders = StringBuilder()
            for (item in columnNames) {
                if (item != "id") {
                    placeholders.append("$item, ")
                }
            }
            return placeholders.append(lastPlaceholder).toString()
        }
    }
}