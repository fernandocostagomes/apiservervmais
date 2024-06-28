package fernandocostagomes.routes.cgd

import fernandocostagomes.routes.idConst
import fernandocostagomes.routes.invalidConst
import fernandocostagomes.routes.playerTribeConst
import fernandocostagomes.routes.playerTribeMoreIdConst
import fernandocostagomes.schemas.cgd.PlayerTribe
import fernandocostagomes.schemas.cgd.ServicePlayerTribe
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRoutingPlayerTribe(servicePlayerTribe: ServicePlayerTribe){

    routing {

        // Create playerPlayerTribe
        authenticate( "auth-jwt") {
            post( playerTribeConst ) {
                call.respond(HttpStatusCode.Created, servicePlayerTribe.create(call.receive<PlayerTribe>()))
            }
            // Delete playerPlayerTribe
            delete( playerTribeMoreIdConst ) {
                val id = call.parameters[ idConst ]?.toInt() ?: throw IllegalArgumentException( invalidConst )
                servicePlayerTribe.delete( id )
                call.respond(HttpStatusCode.OK)
            }

            // List all playerPlayerTribe
            get( playerTribeConst ) {
                call.respond(HttpStatusCode.OK, servicePlayerTribe.list())
            }

            // Read playerPlayerTribe
            get( playerTribeMoreIdConst ) {
                val id = call.parameters[ idConst ]?.toInt() ?: throw IllegalArgumentException( invalidConst )
                try {
                    call.respond(HttpStatusCode.OK, servicePlayerTribe.read(id))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.NotFound)
                }
            }
            // Update playerPlayerTribe
            put( playerTribeMoreIdConst ) {
                val id = call.parameters[ idConst ]?.toInt() ?: throw IllegalArgumentException( invalidConst )
                servicePlayerTribe.update(id, call.receive<PlayerTribe>())
                call.respond(HttpStatusCode.OK)
            }
        }
    }
}