package fernandocostagomes.schemas

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.SQLException
import java.sql.Statement

@Serializable
data class Address(
    val addressId: Int = 0,
    val addressName: String,
    val addressZipcode: String,
    val addressAddress: String,
    val addressNumber: String,
    val addressCity: String,
    val addressState: String,
    val addressUserId: Int,
    val addressDate: String)
class ServiceAddress(private val connection: Connection) : SchemaInterface{
    companion object {
        private const val TABLE = "v_address"
        private const val COLUMN_ID = "v_address_id"
        private const val COLUMN_NAME = "v_address_name"
        private const val COLUMN_ZIPCODE = "v_address_zipcode"
        private const val COLUMN_ADDRESS = "v_address_address"
        private const val COLUMN_NUMBER = "v_address_number"
        private const val COLUMN_CITY = "v_address_city"
        private const val COLUMN_STATE = "v_address_state"
        private const val COLUMN_USER_ID = "v_user_id"
        private const val COLUMN_DATE = "v_address_date"

        private const val COLUMN_ID_QUERY = "$COLUMN_ID SERIAL PRIMARY KEY, "
        private const val COLUMN_NAME_QUERY = "$COLUMN_NAME VARCHAR(20), "
        private const val COLUMN_ZIPCODE_QUERY = "$COLUMN_ZIPCODE VARCHAR(10), "
        private const val COLUMN_ADDRESS_QUERY = "$COLUMN_ADDRESS VARCHAR(40), "
        private const val COLUMN_NUMBER_QUERY = "$COLUMN_NUMBER VARCHAR(8), "
        private const val COLUMN_CITY_QUERY = "$COLUMN_CITY VARCHAR(30), "
        private const val COLUMN_STATE_QUERY = "$COLUMN_STATE VARCHAR(30),"
        private const val COLUMN_USER_ID_QUERY = "$COLUMN_USER_ID INTEGER, "
        private const val COLUMN_DATE_QUERY = "$COLUMN_DATE VARCHAR(20)"


        val listColumnsQuery = listOf(
            COLUMN_ID_QUERY,
            COLUMN_NAME_QUERY,
            COLUMN_ZIPCODE_QUERY,
            COLUMN_ADDRESS_QUERY,
            COLUMN_NUMBER_QUERY,
            COLUMN_CITY_QUERY,
            COLUMN_STATE_QUERY,
            COLUMN_USER_ID_QUERY,
            COLUMN_DATE_QUERY
        )

        val listColumns = listOf(
            COLUMN_ID,
            COLUMN_NAME,
            COLUMN_ZIPCODE,
            COLUMN_ADDRESS,
            COLUMN_NUMBER,
            COLUMN_CITY,
            COLUMN_STATE,
            COLUMN_USER_ID,
            COLUMN_DATE
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
    
    private fun getResultSet(pResultSet: java.sql.ResultSet): Address {
        return Address(
            pResultSet.getInt(COLUMN_ID),
            pResultSet.getString(COLUMN_NAME),
            pResultSet.getString(COLUMN_ZIPCODE),
            pResultSet.getString(COLUMN_ADDRESS),
            pResultSet.getString(COLUMN_NUMBER),
            pResultSet.getString(COLUMN_CITY),
            pResultSet.getString(COLUMN_STATE),
            pResultSet.getInt(COLUMN_USER_ID),
            pResultSet.getString(COLUMN_DATE)
        )
    }
    
    private fun getPreparedStatement(pPreparedStatement: PreparedStatement, pObj: Any): PreparedStatement {        
        pObj as Address
        pPreparedStatement.setInt(1, pObj.addressId)
        pPreparedStatement.setString(2, pObj.addressZipcode)
        pPreparedStatement.setString(3, pObj.addressAddress)
        pPreparedStatement.setString(4, pObj.addressNumber)
        pPreparedStatement.setString(5, pObj.addressCity)
        pPreparedStatement.setString(6, pObj.addressState)
        pPreparedStatement.setInt(7, pObj.addressUserId)
        pPreparedStatement.setString(8, SchemaUtils.getCurrentDate())
        return pPreparedStatement
    }

    // Create new address
    override suspend fun create(obj: Any): Int = withContext(Dispatchers.IO) {

        val statement = connection.prepareStatement(
            SchemaUtils.insertQuery(
                TABLE,
                listColumns
            )
            , Statement.RETURN_GENERATED_KEYS)

        obj as Address

        val statementPos: PreparedStatement = getPreparedStatement( statement, obj )
        statementPos.executeUpdate()

        val generatedKeys = statementPos.generatedKeys

        if (generatedKeys.next()) {
            return@withContext generatedKeys.getInt(1)
        } else {
            throw Exception(SchemaUtils.UNABLE_NEW_ID_INSERTED)
        }
    }

    // Read an address
    override suspend fun read(id: Int): Address = withContext(Dispatchers.IO) {

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
            return@withContext getResultSet( resultSet )            
        } else {
            throw Exception(SchemaUtils.RECORD_NOT_FOUND)
        }
    }

    // Update an address
    override suspend fun update(id: Int, obj: Any) = withContext(Dispatchers.IO) {

        val statement = connection.prepareStatement(
            SchemaUtils.updateQuery(
                TABLE,
                listColumns,
                COLUMN_ID )
        )

        obj as Address

        getPreparedStatement( statement, obj ).executeUpdate()
    }

    // Delete an address
    override suspend fun delete(id: Int) = withContext(Dispatchers.IO) {

        val statement = connection.prepareStatement( "DELETE FROM $TABLE WHERE $COLUMN_ID = ?;" )
        statement.setInt(1, id)
        statement.executeUpdate()
    }

    // List all address
    override suspend fun list(): List<Address> = withContext(Dispatchers.IO) {

        val statement = connection.prepareStatement( "SELECT * FROM $TABLE" )

        val resultSet = statement.executeQuery()

        val addressList = mutableListOf<Address>()

        while (resultSet.next()) {            
            addressList.add( getResultSet( resultSet ) )
        }

        if (addressList.isNotEmpty()) {
            return@withContext addressList
        } else {
            throw Exception(SchemaUtils.RECORD_NOT_FOUND)
        }
    }
}