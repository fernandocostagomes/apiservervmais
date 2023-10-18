package fernandocostagomes.routes

import fernandocostagomes.schemas.Action
import fernandocostagomes.schemas.ServiceAction
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRoutingAction(serviceAction: ServiceAction){

    routing {

        // Create action
        post( actionConst ) {
            val action = call.receive<Action>()
            val id = serviceAction.create(action)
            call.respond(HttpStatusCode.Created, id)
        }

        // Delete action
        delete( actionMoreIdConst ) {
            val id = call.parameters[ idConst ]?.toInt() ?: throw IllegalArgumentException( invalidConst )
            serviceAction.delete(id)
            call.respond(HttpStatusCode.OK)
        }

        // List all action
        get( actionConst ) {
            val listAction = serviceAction.list()
            call.respond(HttpStatusCode.OK, listAction)
        }

        // Read action
        get( actionMoreIdConst ) {
            val id = call.parameters[ idConst ]?.toInt() ?: throw IllegalArgumentException( invalidConst )
            try {
                val action = serviceAction.read(id)
                call.respond(HttpStatusCode.OK, action)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.NotFound)
            }
        }

        // Update action
        put( actionMoreIdConst ) {
            val id = call.parameters[ idConst ]?.toInt() ?: throw IllegalArgumentException( invalidConst )
            val action = call.receive<Action>()
            serviceAction.update(id, action)
            call.respond(HttpStatusCode.OK)
        }


    }
}