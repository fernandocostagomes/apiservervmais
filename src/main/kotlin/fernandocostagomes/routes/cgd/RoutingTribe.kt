package fernandocostagomes.routes.cgd

import fernandocostagomes.routes.idConst
import fernandocostagomes.routes.invalidConst
import fernandocostagomes.routes.tribeConst
import fernandocostagomes.routes.tribeMoreIdConst
import fernandocostagomes.schemas.cgd.ServiceTribe
import fernandocostagomes.schemas.cgd.Tribe
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRoutingTribe(serviceTribe: ServiceTribe){

    routing {

        // Create tribe
        authenticate( "auth-jwt") {
            post( tribeConst ) {
                call.respond(HttpStatusCode.Created, serviceTribe.create(call.receive<Tribe>()))
            }
            // Delete tribe
            delete( tribeMoreIdConst ) {
                val id = call.parameters[ idConst ]?.toInt() ?: throw IllegalArgumentException( invalidConst )
                serviceTribe.delete( id )
                call.respond(HttpStatusCode.OK)
            }

            // List all tribe
            get( tribeConst ) {
                call.respond(HttpStatusCode.OK, serviceTribe.list())
            }

            // Read tribe
            get( tribeMoreIdConst ) {
                val id = call.parameters[ idConst ]?.toInt() ?: throw IllegalArgumentException( invalidConst )
                try {
                    call.respond(HttpStatusCode.OK, serviceTribe.read(id))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.NotFound)
                }
            }
            // Update tribe
            put( tribeMoreIdConst ) {
                val id = call.parameters[ idConst ]?.toInt() ?: throw IllegalArgumentException( invalidConst )
                serviceTribe.update(id, call.receive<Tribe>())
                call.respond(HttpStatusCode.OK)
            }
        }
    }
}