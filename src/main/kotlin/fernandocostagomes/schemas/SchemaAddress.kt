package fernandocostagomes.schemas

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import java.sql.Connection
import java.sql.SQLException
import java.sql.Statement

@Serializable
data class Address(
    val nameAddress: String,
    val codeAddress: Int,
    val addressAddress: String,
    val numberAddress: String,
    val cityAddress: String,
    val stateAddress: String)
class ServiceAddress(private val connection: Connection) : SchemaInterface{
    companion object {
        private const val TABLE = "v_address"
        private const val COLUMN_ID = "v_address_id"
        private const val COLUMN_NAME = "v_address_name"
        private const val COLUMN_CODE = "v_address_code"
        private const val COLUMN_ADDRESS = "v_address_address"
        private const val COLUMN_NUMBER = "v_address_number"
        private const val COLUMN_CITY = "v_address_city"
        private const val COLUMN_STATE = "v_address_state"

        private const val CREATE_TABLE_ADDRESS =
                "CREATE TABLE IF NOT EXISTS " +
                        "$TABLE (" +
                        "$COLUMN_ID SERIAL PRIMARY KEY, " +
                        "$COLUMN_NAME VARCHAR(20), " +
                        "$COLUMN_CODE INTEGER NOT NULL, " +
                        "$COLUMN_ADDRESS VARCHAR(40)," +
                        "$COLUMN_NUMBER VARCHAR(8)," +
                        "$COLUMN_CITY VARCHAR(30)," +
                        "$COLUMN_STATE VARCHAR(30));"

        private const val SELECT_ADDRESS_BY_ID = "SELECT " +
                "$COLUMN_NAME, " +
                "$COLUMN_CODE, " +
                "$COLUMN_ADDRESS, " +
                "$COLUMN_NUMBER, " +
                "$COLUMN_CITY, " +
                "$COLUMN_STATE " +
                "FROM $TABLE WHERE $COLUMN_ID = ?;"

        private const val INSERT_ADDRESS = "INSERT INTO $TABLE (" +
                "$COLUMN_NAME, " +
                "$COLUMN_CODE, " +
                "$COLUMN_ADDRESS, " +
                "$COLUMN_NUMBER, " +
                "$COLUMN_CITY, " +
                "$COLUMN_STATE) VALUES (?, ?, ?, ?, ?, ?);"

        private const val UPDATE_ADDRESS = "UPDATE $TABLE SET " +
                "$COLUMN_NAME = ?," +
                "$COLUMN_CODE = ?, " +
                "$COLUMN_ADDRESS = ? " +
                "$COLUMN_NUMBER = ? " +
                "$COLUMN_CITY = ? " +
                "$COLUMN_STATE = ? " +
                "WHERE $COLUMN_ID = ?;"

        private const val DELETE_ADDRESS = "DELETE FROM $TABLE WHERE $COLUMN_ID = ?;"

        private const val LIST_ADDRESS = "SELECT * FROM $TABLE"
    }

    init {
        try {
            val statement = connection.createStatement()
            statement.executeUpdate(CREATE_TABLE_ADDRESS)
        } catch (e: SQLException) {
            println(e.toString())
        }
    }

    // Create new address
    override suspend fun create(obj: Any): Int = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(INSERT_ADDRESS, Statement.RETURN_GENERATED_KEYS)
        obj as Address
        statement.setString(1, obj.nameAddress)
        statement.setInt(2, obj.codeAddress)
        statement.setString(3, obj.addressAddress)
        statement.setString(4, obj.numberAddress)
        statement.setString(5, obj.cityAddress)
        statement.setString(6, obj.stateAddress)
        statement.executeUpdate()

        val generatedKeys = statement.generatedKeys
        if (generatedKeys.next()) {
            return@withContext generatedKeys.getInt(1)
        } else {
            throw Exception("Unable to retrieve the id of the newly inserted address")
        }
    }

    // Read an address
    override suspend fun read(id: Int): Address = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(SELECT_ADDRESS_BY_ID)
        statement.setInt(1, id)
        val resultSet = statement.executeQuery()

        if (resultSet.next()) {
            val name = resultSet.getString( COLUMN_NAME )
            val code = resultSet.getInt( COLUMN_CODE )
            val address = resultSet.getString( COLUMN_ADDRESS)
            val number = resultSet.getString( COLUMN_NUMBER )
            val city = resultSet.getString( COLUMN_CITY )
            val state = resultSet.getString( COLUMN_STATE )
            return@withContext Address(name, code, address, number, city, state)
        } else {
            throw Exception("Record not found")
        }
    }

    // Update an address
    override suspend fun update(id: Int, obj: Any) = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(UPDATE_ADDRESS)
        obj as Address
        statement.setInt(0, id)
        statement.setString(1, obj.nameAddress)
        statement.setInt(2, obj.codeAddress)
        statement.setString(3, obj.addressAddress)
        statement.setString(4, obj.numberAddress)
        statement.setString(5, obj.cityAddress)
        statement.setString(6, obj.stateAddress)
        statement.executeUpdate()
    }

    // Delete an address
    override suspend fun delete(id: Int) = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement( DELETE_ADDRESS )
        statement.setInt(1, id)
        statement.executeUpdate()
    }

    // List all address
    override suspend fun list(): List<Address> = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement( LIST_ADDRESS )
        val resultSet = statement.executeQuery()

        val addressList = mutableListOf<Address>()

        while (resultSet.next()) {
            val nameAddress = resultSet.getString( COLUMN_NAME )
            val codeAddress = resultSet.getInt( COLUMN_CODE )
            val addressAddress = resultSet.getString( COLUMN_ADDRESS )
            val numberAddress = resultSet.getString( COLUMN_NUMBER )
            val cityAddress = resultSet.getString( COLUMN_CITY )
            val stateAddress = resultSet.getString( COLUMN_STATE )

            val address = Address(
                nameAddress,
                codeAddress,
                addressAddress,
                numberAddress,
                cityAddress,
                stateAddress
            )
            addressList.add( address )
        }

        if (addressList.isNotEmpty()) {
            return@withContext addressList
        } else {
            throw Exception("No records found")
        }
    }
}