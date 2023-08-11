package fernandocostagomes.routes

import fernandocostagomes.models.Role
import fernandocostagomes.models.ServiceRole
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRoutingRole(serviceRole: ServiceRole){

    routing {

        // Create role
        post( roleConst ) {
            val role = call.receive<Role>()
            val id = serviceRole.create(role)
            call.respond(HttpStatusCode.Created, id)
        }

        // Delete role
        delete( roleMoreIdConst ) {
            val id = call.parameters[ idConst ]?.toInt() ?: throw IllegalArgumentException( invalidConst )
            serviceRole.delete(id)
            call.respond(HttpStatusCode.OK)
        }

        // List all role
        get( roleConst ) {
            val listRole = serviceRole.list()
            call.respond(HttpStatusCode.OK, listRole)
        }

        // Read role
        get( roleMoreIdConst ) {
            val id = call.parameters[ idConst ]?.toInt() ?: throw IllegalArgumentException( invalidConst )
            try {
                val role = serviceRole.read(id)
                call.respond(HttpStatusCode.OK, role)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.NotFound)
            }
        }

        // Update role
        put( roleMoreIdConst ) {
            val id = call.parameters[ idConst ]?.toInt() ?: throw IllegalArgumentException( invalidConst )
            val role = call.receive<Role>()
            serviceRole.update(id, role)
            call.respond(HttpStatusCode.OK)
        }
    }
}