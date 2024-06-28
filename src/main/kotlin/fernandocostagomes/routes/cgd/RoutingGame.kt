package fernandocostagomes.routes.cgd

import fernandocostagomes.routes.gameConst
import fernandocostagomes.routes.gameMoreIdConst
import fernandocostagomes.routes.idConst
import fernandocostagomes.routes.invalidConst
import fernandocostagomes.schemas.cgd.Game
import fernandocostagomes.schemas.cgd.ServiceGame
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRoutingGame(serviceGame: ServiceGame){

    routing {

        // Create game
        authenticate( "auth-jwt") {
            post( gameConst ) {
                call.respond(HttpStatusCode.Created, serviceGame.create(call.receive<Game>()))
            }
            // Delete game
            delete( gameMoreIdConst ) {
                val id = call.parameters[ idConst ]?.toInt() ?: throw IllegalArgumentException( invalidConst )
                serviceGame.delete( id )
                call.respond(HttpStatusCode.OK)
            }

            // List all game
            get( gameConst ) {
                call.respond(HttpStatusCode.OK, serviceGame.list())
            }

            // Read game
            get( gameMoreIdConst ) {
                val id = call.parameters[ idConst ]?.toInt() ?: throw IllegalArgumentException( invalidConst )
                try {
                    call.respond(HttpStatusCode.OK, serviceGame.read(id))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.NotFound)
                }
            }
            // Update game
            put( gameMoreIdConst ) {
                val id = call.parameters[ idConst ]?.toInt() ?: throw IllegalArgumentException( invalidConst )
                serviceGame.update(id, call.receive<Game>())
                call.respond(HttpStatusCode.OK)
            }
        }
    }
}