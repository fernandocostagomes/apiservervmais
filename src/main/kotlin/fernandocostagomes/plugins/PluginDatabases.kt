package fernandocostagomes.plugins

import fernandocostagomes.routes.*
import fernandocostagomes.routes.cgd.*
import fernandocostagomes.schemas.*
import fernandocostagomes.schemas.cgd.*
import io.ktor.server.application.*
import java.sql.Connection
import java.sql.DriverManager

fun Application.configureDatabases() {

    val hashSet = hashMapOf<String, String>()
    hashSet["url"] = System.getenv("POSTGRES_URL")
    hashSet["port"] = System.getenv("POSTGRES_PORT")
    hashSet["user"] = System.getenv("POSTGRES_USER")
    hashSet["pwd"] = System.getenv("POSTGRES_PASSWORD")
    hashSet["db"] = System.getenv("POSTGRES_DB")

    val dbConnection: Connection = connectToPostgres(embedded = true, hashSet)

    configureRoutingLogin( ServiceUser(dbConnection), ServicePwd(dbConnection))
    configureRoutingAddress( ServiceAddress(dbConnection) )
    configureRoutingAction( ServiceAction(dbConnection) )
    configureRoutingGroup( ServiceGroup(dbConnection) )
    configureRoutingParameter( ServiceParameter(dbConnection) )
    configureRoutingPermission( ServicePermission(dbConnection) )
    configureRoutingRole( ServiceRole(dbConnection) )
    configureRoutingUser( ServiceUser(dbConnection) )

    configureRoutingPlayer( ServicePlayer(dbConnection ) )
    configureRoutingTribe( ServiceTribe(dbConnection ) )
    configureRoutingGame( ServiceGame( dbConnection ) )
    configureRoutingPunctuation( ServicePunctuation( dbConnection ) )
    configureRoutingPermissionCgd( ServicePermissionCgd( dbConnection ) )
    configureRoutingPlayerTribe( ServicePlayerTribe( dbConnection ) )
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
fun connectToPostgres(embedded: Boolean, hashMap: HashMap<String, String>): Connection {

    Class.forName("org.postgresql.Driver")

    return if (embedded) {
        DriverManager.getConnection("jdbc:postgresql://${hashMap["url"]}:${hashMap["port"]}/${hashMap["db"]}", hashMap["user"], hashMap["pwd"] )
    } else {
        DriverManager.getConnection(hashMap["url"], hashMap["user"], hashMap["pwd"])
    }
}