package fernandocostagomes.routes.cgd

import fernandocostagomes.routes.idConst
import fernandocostagomes.routes.invalidConst
import fernandocostagomes.routes.playerConst
import fernandocostagomes.routes.playerMoreIdConst
import fernandocostagomes.schemas.cgd.Player
import fernandocostagomes.schemas.cgd.ServicePlayer
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRoutingPlayer(servicePlayer: ServicePlayer){

    routing {

        // Create player
        authenticate( "auth-jwt") {
            post( playerConst ) {
                call.respond(HttpStatusCode.Created, servicePlayer.create(call.receive<Player>()))
            }
            // Delete player
            delete( playerMoreIdConst ) {
                val id = call.parameters[ idConst ]?.toInt() ?: throw IllegalArgumentException( invalidConst )
                servicePlayer.delete( id )
                call.respond(HttpStatusCode.OK)
            }

            // List all player
            get( playerConst ) {
                call.respond(HttpStatusCode.OK, servicePlayer.list())
            }

            // Read player
            get( playerMoreIdConst ) {
                val id = call.parameters[ idConst ]?.toInt() ?: throw IllegalArgumentException( invalidConst )
                try {
                    call.respond(HttpStatusCode.OK, servicePlayer.read(id))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.NotFound)
                }
            }
            // Update player
            put( playerMoreIdConst ) {
                val id = call.parameters[ idConst ]?.toInt() ?: throw IllegalArgumentException( invalidConst )
                servicePlayer.update(id, call.receive<Player>())
                call.respond(HttpStatusCode.OK)
            }
        }
    }
}