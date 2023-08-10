package fernandocostagomes.plugins

import fernandocostagomes.models.*
import fernandocostagomes.routes.*
import io.ktor.server.application.*
import java.sql.Connection
import java.sql.DriverManager

fun Application.configureDatabases() {

    val dbConnection: Connection = connectToPostgres(embedded = true)

    val addressService = AddressService(dbConnection)
    val groupService = GroupService(dbConnection)
    val parameterService = ParameterService(dbConnection)
    val userService = UserService(dbConnection)

    configureRoutingAddress(addressService)
    configureRoutingGroup(groupService)
    configureRoutingParameter(parameterService)
    configureRoutingUser(userService)
}

/**
 * Makes a connection to a Postgres database.
 *
 * In order to connect to your running Postgres process,
 * please specify the following parameters in your configuration file:
 * - postgres.url -- Url of your running database process.
 * - postgres.User -- Username for database connection
 * - postgres.Password -- Password for database connection
 *
 * If you don't have a database process running yet, you may need to [download]((https://www.postgresql.org/download/))
 * and install Postgres and follow the instructions [here](https://postgresapp.com/).
 * Then, you would be able to edit your url,  which is usually "jdbc:postgresql://host:port/database", as well as
 * user and password values.
 *
 *
 * @param embedded -- if true defaults to an embedded database for tests that runs locally in the same process.
 * In this case you don't have to provide any parameters in configuration file, and you don't have to run a process.
 *
 * @return [Connection] that represent connection to the database. Please, don't forget to close this connection when
 * your application shuts down by calling [Connection.close]
 * */
fun connectToPostgres(embedded: Boolean): Connection {

    val url = "127.0.0.1"
    val db = "dbvmaispostgres"
    val port = "5432"
    val user = "postgres"
    val pwd = "cgdpwd"

    Class.forName("org.postgresql.Driver")

    return if (embedded) {
        DriverManager.getConnection("jdbc:postgresql://$url:$port/$db", user, pwd)
    } else {
//        val url = environment.config.property("postgres.url").getString()
//        val user = environment.config.property("postgres.user").getString()
//        val password = environment.config.property("postgres.password").getString()

        DriverManager.getConnection(url, user, pwd)
    }
}