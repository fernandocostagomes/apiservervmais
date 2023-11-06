package fernandocostagomes.routes

import fernandocostagomes.schemas.Permission
import fernandocostagomes.schemas.ServicePermission
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRoutingPermission(servicePermission: ServicePermission){

    routing {
        authenticate( "auth-jwt") {

            // Create permission
            post( permissionConst ) {
                val permission = call.receive<Permission>()
                val id = servicePermission.create(permission)
                call.respond(HttpStatusCode.Created, id)
            }

            // Delete permission
            delete( permissionMoreIdConst ) {
                val id = call.parameters[ idConst ]?.toInt() ?: throw IllegalArgumentException( invalidConst )
                servicePermission.delete(id)
                call.respond(HttpStatusCode.OK)
            }

            // List all permission
            get( permissionConst ) {
                val listPermission = servicePermission.list()
                call.respond(HttpStatusCode.OK, listPermission)
            }

            // Read permission
            get( permissionMoreIdConst ) {
                val id = call.parameters[ idConst ]?.toInt() ?: throw IllegalArgumentException( invalidConst )
                try {
                    val permission = servicePermission.read(id)
                    call.respond(HttpStatusCode.OK, permission)
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.NotFound)
                }
            }

            // Update permission
            put( permissionMoreIdConst ) {
                val id = call.parameters[ idConst ]?.toInt() ?: throw IllegalArgumentException( invalidConst )
                val permission = call.receive<Permission>()
                servicePermission.update(id, permission)
                call.respond(HttpStatusCode.OK)
            }
        }
    }
}