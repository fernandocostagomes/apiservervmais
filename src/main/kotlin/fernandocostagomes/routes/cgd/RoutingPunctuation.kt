package fernandocostagomes.routes.cgd

import fernandocostagomes.routes.idConst
import fernandocostagomes.routes.invalidConst
import fernandocostagomes.routes.punctuationConst
import fernandocostagomes.routes.punctuationMoreIdConst
import fernandocostagomes.schemas.cgd.Punctuation
import fernandocostagomes.schemas.cgd.ServicePunctuation
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRoutingPunctuation(servicePunctuation: ServicePunctuation){

    routing {

        // Create punctuation
        authenticate( "auth-jwt") {
            post( punctuationConst ) {
                call.respond(HttpStatusCode.Created, servicePunctuation.create(call.receive<Punctuation>()))
            }
            // Delete punctuation
            delete( punctuationMoreIdConst ) {
                val id = call.parameters[ idConst ]?.toInt() ?: throw IllegalArgumentException( invalidConst )
                servicePunctuation.delete( id )
                call.respond(HttpStatusCode.OK)
            }

            // List all punctuation
            get( punctuationConst ) {
                call.respond(HttpStatusCode.OK, servicePunctuation.list())
            }

            // Read punctuation
            get( punctuationMoreIdConst ) {
                val id = call.parameters[ idConst ]?.toInt() ?: throw IllegalArgumentException( invalidConst )
                try {
                    call.respond(HttpStatusCode.OK, servicePunctuation.read(id))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.NotFound)
                }
            }
            // Update punctuation
            put( punctuationMoreIdConst ) {
                val id = call.parameters[ idConst ]?.toInt() ?: throw IllegalArgumentException( invalidConst )
                servicePunctuation.update(id, call.receive<Punctuation>())
                call.respond(HttpStatusCode.OK)
            }
        }
    }
}