package fernandocostagomes.routes.cgd

import fernandocostagomes.routes.*
import fernandocostagomes.schemas.cgd.Permission
import fernandocostagomes.schemas.cgd.ServicePermissionCgd
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRoutingPermissionCgd(servicePermission: ServicePermissionCgd){

    routing {

        // Create permission
        authenticate( "auth-jwt") {
            post( cgdConst + permissionConst ) {
                call.respond(HttpStatusCode.Created, servicePermission.create(call.receive<Permission>()))
            }
            // Delete permission
            delete( cgdConst + permissionMoreIdConst ) {
                val id = call.parameters[ idConst ]?.toInt() ?: throw IllegalArgumentException( invalidConst )
                servicePermission.delete( id )
                call.respond(HttpStatusCode.OK)
            }

            // List all permission
            get( cgdConst + permissionConst ) {
                call.respond(HttpStatusCode.OK, servicePermission.list())
            }

            // Read permission
            get( cgdConst + permissionMoreIdConst ) {
                val id = call.parameters[ idConst ]?.toInt() ?: throw IllegalArgumentException( invalidConst )
                try {
                    call.respond(HttpStatusCode.OK, servicePermission.read(id))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.NotFound)
                }
            }
            // Update permission
            put( cgdConst + permissionMoreIdConst ) {
                val id = call.parameters[ idConst ]?.toInt() ?: throw IllegalArgumentException( invalidConst )
                servicePermission.update(id, call.receive<Permission>())
                call.respond(HttpStatusCode.OK)
            }
        }
    }
}