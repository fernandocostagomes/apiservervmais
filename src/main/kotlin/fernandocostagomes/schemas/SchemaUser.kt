package fernandocostagomes.schemas

import io.ktor.client.*
import io.ktor.server.routing.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Statement

@Serializable
data class User(
    val userId: Int = 0,
    var userEmail: String,
    var userName: String,
    var userPhone: String,
    var userNick: String,
    var userBirthday: String,
    var userDate: String,
    var userPwdCurrent: String,
    var userPwdId: Int)

class ServiceUser(private val connection: Connection) : SchemaInterface {
    companion object {
        private const val TABLE = "v_user"
        private const val COLUMN_ID = "v_user_id"
        private const val COLUMN_EMAIL = "v_user_email"
        private const val COLUMN_NAME = "v_user_name"
        private const val COLUMN_PHONE = "v_user_phone"
        private const val COLUMN_NICK = "v_user_nick"
        private const val COLUMN_BIRTHDAY = "v_user_birthday"
        private const val COLUMN_DATE = "v_user_date"
        private const val COLUMN_PWD_CURRENT = "v_user_pwd_current"
        private const val COLUMN_PWD_ID = "v_user_pwd_id"


        private const val COLUMN_ID_QUERY = "$COLUMN_ID SERIAL PRIMARY KEY, "
        private const val COLUMN_EMAIL_QUERY = "$COLUMN_EMAIL VARCHAR(50) NOT NULL, "
        private const val COLUMN_NAME_QUERY = "$COLUMN_NAME VARCHAR(30), "
        private const val COLUMN_PHONE_QUERY = "$COLUMN_PHONE VARCHAR(13) NOT NULL, "
        private const val COLUMN_NICK_QUERY = "$COLUMN_NICK VARCHAR(20), "
        private const val COLUMN_BIRTHDAY_QUERY = "$COLUMN_BIRTHDAY VARCHAR(20), "
        private const val COLUMN_DATE_QUERY = "$COLUMN_DATE VARCHAR(20), "
        private const val COLUMN_PWD_CURRENT_QUERY = "$COLUMN_PWD_CURRENT VARCHAR(20) NOT NULL, "
        private const val COLUMN_PWD_ID_QUERY = "$COLUMN_PWD_ID INTEGER"

        val listColumnsQuery = listOf(
            COLUMN_ID_QUERY,
            COLUMN_EMAIL_QUERY,
            COLUMN_NAME_QUERY,
            COLUMN_PHONE_QUERY,
            COLUMN_NICK_QUERY,
            COLUMN_BIRTHDAY_QUERY,
            COLUMN_DATE_QUERY,
            COLUMN_PWD_CURRENT_QUERY,
            COLUMN_PWD_ID_QUERY
        )

        val listColumns = listOf(
            COLUMN_ID,
            COLUMN_EMAIL,
            COLUMN_NAME,
            COLUMN_PHONE,
            COLUMN_NICK,
            COLUMN_BIRTHDAY,
            COLUMN_DATE,
            COLUMN_PWD_CURRENT,
            COLUMN_PWD_ID
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

    private fun getResultSet(pResultSet: ResultSet): User {
        return User(
            pResultSet.getInt(COLUMN_ID),
            pResultSet.getString(COLUMN_EMAIL),
            pResultSet.getString(COLUMN_NAME),
            pResultSet.getString(COLUMN_PHONE),
            pResultSet.getString(COLUMN_NICK),
            pResultSet.getString(COLUMN_BIRTHDAY),
            pResultSet.getString(COLUMN_DATE),
            pResultSet.getString(COLUMN_PWD_CURRENT),
            pResultSet.getInt(COLUMN_PWD_ID)
        )
    }

    private fun getPreparedStatement(pPreparedStatement: PreparedStatement,  pObj: Any): PreparedStatement{
        pObj as User
        pPreparedStatement.setString(1, pObj.userEmail)
        pPreparedStatement.setString(2, pObj.userName)
        pPreparedStatement.setString(3, pObj.userPhone)
        pPreparedStatement.setString(4, pObj.userNick)
        pPreparedStatement.setString(5, pObj.userBirthday)
        pPreparedStatement.setString(6, SchemaUtils.getCurrentDate())
        pPreparedStatement.setString(7, pObj.userPwdCurrent)
        pPreparedStatement.setInt(8, pObj.userPwdId)
        return pPreparedStatement
    }

    // Create new user
    override suspend fun create( obj: Any ): Int = withContext(Dispatchers.IO) {
        obj as User

        val userId = createUserDb( obj )
        println( "Schema userId: $userId" )

        //Cria a senha
        //Valida se o id é diferente de 0
        if ( userId > 0) {

            val pwd = Pwd(
                pwdId = 0,
                pwdUserId = userId,
                pwdCurrent = obj.userPwdCurrent,
                pwdLast = "",
                pwdMoreLast = "",
                pwdDate = SchemaUtils.getCurrentDate()
            )

            val pwdId = createPwd( pwd )
            println( "Schema pwdId: $pwdId" )

            if ( pwdId > 0 ) {

                //Atualiza o usuario com o id da senha
                var user: User = read( userId )

                user.userPwdId = pwdId

                //Imprime o user.
                println( user )


                if( update( userId, user ) == 1 ) {
                    return@withContext userId
                } else {
                    throw Exception( SchemaUtils.UNABLE_NEW_ID_INSERTED )
                }

            } else {
                throw Exception( SchemaUtils.UNABLE_NEW_ID_INSERTED )
            }
        } else {
            throw Exception( SchemaUtils.UNABLE_NEW_ID_INSERTED )
        }
    }

    private suspend fun createUserDb(obj: Any ): Int = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(
            SchemaUtils.insertQuery(
                TABLE,
                listColumns
            ),
            Statement.RETURN_GENERATED_KEYS
        )

        obj as User

        val statementPos = getPreparedStatement(statement, obj)
        statementPos.executeUpdate()

        val generatedKeys = statementPos.generatedKeys

        if (generatedKeys.next()) {
            return@withContext generatedKeys.getInt(1)
        } else {
            throw Exception(SchemaUtils.UNABLE_NEW_ID_INSERTED)
        }
    }

    private suspend fun createPwd( pwd: Pwd ): Int = withContext( Dispatchers.IO ) {

        val servicePwd = ServicePwd( connection )

        val pwdId = servicePwd.create( pwd )

        if( pwdId > 0 ) {
            return@withContext pwdId
        } else {
            throw Exception( SchemaUtils.UNABLE_NEW_ID_INSERTED )
        }
    }

    // Read a user
    override suspend fun read(id: Int): User = withContext(Dispatchers.IO) {

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

    // Update a user
    override suspend fun update( id: Int, obj: Any ) = withContext(Dispatchers.IO) {

        val query = SchemaUtils.updateQuery(
            TABLE,
            listColumns,
            COLUMN_ID
        )

        println( query )

        val statement = connection.prepareStatement( query )

        obj as User

        val statementPos = getPreparedStatement(statement, obj)

        println( statementPos.toString() )
        statementPos.setInt(0, id)
        statementPos.executeUpdate()

        // Retorna o id do usuario se nao tiver dado erro.
        if ( id == 1 ) {
            return@withContext id
        } else {
            throw Exception( SchemaUtils.UNABLE_NEW_ID_INSERTED )
        }
    }

    // Delete a user
    override suspend fun delete(id: Int) = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement( "DELETE FROM $TABLE WHERE $COLUMN_ID = ?" )
        statement.setInt(1, id)
        statement.executeUpdate()
    }

    override suspend fun list(): List<User> = withContext(Dispatchers.IO) {

        val statement = connection.prepareStatement( "SELECT * FROM $TABLE" )

        val resultSet = statement.executeQuery()

        val userList = mutableListOf<User>()

        while (resultSet.next()) {
            userList.add( getResultSet(resultSet) )
        }

        if (userList.isNotEmpty()) {
            return@withContext userList
        } else {
            throw Exception(SchemaUtils.RECORD_NOT_FOUND)
        }
    }

    // Read a user for email.
    suspend fun read(email: String): User = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(
            SchemaUtils.selectQuery(
                TABLE,
                COLUMN_EMAIL,
                listColumns
            )
        )

        statement.setString(1, email)

        val resultSet = statement.executeQuery()

        if (resultSet.next()) {
            return@withContext getResultSet( resultSet )
        } else {
            throw Exception(SchemaUtils.RECORD_NOT_FOUND)
        }
    }
}
