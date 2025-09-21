package fernandocostagomes.routes

import fernandocostagomes.schemas.Logs
import fernandocostagomes.schemas.ServiceLogs
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.swagger.v3.oas.annotations.logss.RequestBody

fun Application.configureRoutingLogs(serviceLogs: ServiceLogs){

    routing {
        authenticate( "auth-jwt") {

            // Create logs
            post( logsConst ) {
                try {
                    val logs = call.receive<Logs>()
                    val id = serviceLogs.create(logs)
                    call.respond(HttpStatusCode.Created, id)
                }catch (e: Exception){
                    call.respond(HttpStatusCode.BadRequest)
                }
            }

            // Delete logs
            delete( logsMoreIdConst ) {
                val id = call.logss[ idConst ]?.toInt() ?: throw IllegalArgumentException( invalidConst )
                serviceLogs.delete(id)
                call.respond(HttpStatusCode.OK)
            }

            // List all logs
            get( logsConst ) {
                val listLogs = serviceLogs.list()
                call.respond(HttpStatusCode.OK, listLogs)
            }

            // Read logs
            get( logsMoreIdConst ) {
                val id = call.logss[ idConst ]?.toInt() ?: throw IllegalArgumentException( invalidConst )
                try {
                    val logs = serviceLogs.read(id)
                    call.respond(HttpStatusCode.OK, logs)
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.NotFound)
                }
            }

            // Update logs
            put( logsMoreIdConst ) {
                val id = call.logss[ idConst ]?.toInt() ?: throw IllegalArgumentException( invalidConst )
                val logs = call.receive<Logs>()
                serviceLogs.update(id, logs)
                call.respond(HttpStatusCode.OK)
            }
        }
    }
}