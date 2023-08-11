package fernandocostagomes.models

interface SchemaInterface {
    suspend fun create(obj: Any): Int
    suspend fun delete(id: Int): Int
    suspend fun list(): List<Any>
    suspend fun read(id: Int): Any
    suspend fun update(id: Int, obj: Any): Int
}