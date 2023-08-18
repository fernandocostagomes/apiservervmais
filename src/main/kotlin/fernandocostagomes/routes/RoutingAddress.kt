package fernandocostagomes.routes

import fernandocostagomes.schemas.Address
import fernandocostagomes.schemas.ServiceAddress
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRoutingAddress(serviceAddress: ServiceAddress){

    routing {

        // Create address
        post( addressConst ) {
            val address = call.receive<Address>()
            val id = serviceAddress.create(address)
            call.respond(HttpStatusCode.Created, id)
        }

        // Delete address
        delete( addressMoreIdConst ) {
            val id = call.parameters[ idConst ]?.toInt() ?: throw IllegalArgumentException( invalidConst )
            serviceAddress.delete( id )
            call.respond(HttpStatusCode.OK)
        }

        // List all address
        get( addressConst ) {
            val listAddress = serviceAddress.list()
            call.respond(HttpStatusCode.OK, listAddress)
        }

        // Read address
        get( addressMoreIdConst ) {
            val id = call.parameters[ idConst ]?.toInt() ?: throw IllegalArgumentException( invalidConst )
            try {
                val address = serviceAddress.read(id)
                call.respond(HttpStatusCode.OK, address)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.NotFound)
            }
        }
        // Update address
        put( addressMoreIdConst ) {
            val id = call.parameters[ idConst ]?.toInt() ?: throw IllegalArgumentException( invalidConst )
            val address = call.receive<Address>()
            serviceAddress.update(id, address)
            call.respond(HttpStatusCode.OK)
        }

    }
}