package fernandocostagomes.routes

import fernandocostagomes.schemas.Group
import fernandocostagomes.schemas.ServiceGroup
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRoutingGroup(serviceGroup: ServiceGroup){

    routing {
        authenticate( "auth-jwt") {

            // Create group
            post( groupConst ) {
                val group = call.receive<Group>()
                val id = serviceGroup.create(group)
                call.respond(HttpStatusCode.Created, id)
            }

            // Delete group
            delete( groupMoreIdConst ) {
                val id = call.parameters[ idConst ]?.toInt() ?: throw IllegalArgumentException( invalidConst )
                serviceGroup.delete(id)
                call.respond(HttpStatusCode.OK)
            }

            // List all group
            get( groupConst ) {
                val listTeam = serviceGroup.list()
                call.respond(HttpStatusCode.OK, listTeam)
            }

            // Read group
            get( groupMoreIdConst ) {
                val id = call.parameters[ idConst ]?.toInt() ?: throw IllegalArgumentException( invalidConst )
                try {
                    val group = serviceGroup.read(id)
                    call.respond(HttpStatusCode.OK, group)
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.NotFound)
                }
            }

            // Update group
            put( groupMoreIdConst ) {
                val id = call.parameters[ idConst ]?.toInt() ?: throw IllegalArgumentException( invalidConst )
                val group = call.receive<Group>()
                serviceGroup.update(id, group)
                call.respond(HttpStatusCode.OK)
            }
        }
    }
}