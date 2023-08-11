package fernandocostagomes.routes

import fernandocostagomes.models.Parameter
import fernandocostagomes.models.ServiceParameter
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRoutingParameter(serviceParameter: ServiceParameter){

    routing {

        // Create parameter
        post( parameterConst ) {
            val parameter = call.receive<Parameter>()
            val id = serviceParameter.create(parameter)
            call.respond(HttpStatusCode.Created, id)
        }

        // Delete parameter
        delete( parameterMoreIdConst ) {
            val id = call.parameters[ idConst ]?.toInt() ?: throw IllegalArgumentException( invalidConst )
            serviceParameter.delete(id)
            call.respond(HttpStatusCode.OK)
        }

        // List all parameter
        get( parameterConst ) {
            val listParameter = serviceParameter.list()
            call.respond(HttpStatusCode.OK, listParameter)
        }

        // Read parameter
        get( parameterMoreIdConst ) {
            val id = call.parameters[ idConst ]?.toInt() ?: throw IllegalArgumentException( invalidConst )
            try {
                val parameter = serviceParameter.read(id)
                call.respond(HttpStatusCode.OK, parameter)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.NotFound)
            }
        }

        // Update parameter
        put( parameterMoreIdConst ) {
            val id = call.parameters[ idConst ]?.toInt() ?: throw IllegalArgumentException( invalidConst )
            val parameter = call.receive<Parameter>()
            serviceParameter.update(id, parameter)
            call.respond(HttpStatusCode.OK)
        }


    }
}