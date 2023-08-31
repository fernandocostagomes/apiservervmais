package fernandocostagomes.routes
import fernandocostagomes.schemas.User
import fernandocostagomes.schemas.ServiceUser
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRoutingUser(serviceUser: ServiceUser){

    routing {

        // Create user
        post( userConst ) {
            val user = call.receive<User>()
            val id = serviceUser.create(user)
            call.respond(HttpStatusCode.Created, id)
        }

        // Delete user
        delete( userMoreIdConst ) {
            val id = call.parameters[ idConst ]?.toInt() ?: throw IllegalArgumentException( invalidConst )
            serviceUser.delete(id)
            call.respond(HttpStatusCode.OK)
        }

        // List all user
        get( userConst ) {
            val listUser = serviceUser.list()
            call.respond(HttpStatusCode.OK, listUser)
        }

        // Read user
        get( userMoreIdConst ) {
            val id = call.parameters[ idConst ]?.toInt() ?: throw IllegalArgumentException( invalidConst )
            try {
                val user = serviceUser.read(id)
                call.respond(HttpStatusCode.OK, user)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.NotFound)
            }
        }

        // Update user
        put( userMoreIdConst ) {
            val id = call.parameters[ idConst ]?.toInt() ?: throw IllegalArgumentException( invalidConst )
            val user = call.receive<User>()
            serviceUser.update(id, user)
            call.respond(HttpStatusCode.OK)
        }
    }
}