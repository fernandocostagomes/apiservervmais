package fernandocostagomes.routes

import fernandocostagomes.schemas.Parameter
import fernandocostagomes.schemas.ServiceParameter
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.swagger.v3.oas.annotations.parameters.RequestBody

fun Application.configureRoutingParameter(serviceParameter: ServiceParameter){

    routing {
        authenticate( "auth-jwt") {

            // Create parameter
            post( parameterConst ) {
                try {
                    val parameter = call.receive<Parameter>()
                    val id = serviceParameter.create(parameter)
                    call.respond(HttpStatusCode.Created, id)
                }catch (e: Exception){
                    call.respond(HttpStatusCode.BadRequest)
                }
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
}