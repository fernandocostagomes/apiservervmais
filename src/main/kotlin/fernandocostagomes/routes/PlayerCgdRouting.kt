package fernandocostagomes.routes

import fernandocostagomes.models.Group
import fernandocostagomes.models.GroupService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRoutingGroup(groupService: GroupService){
    routing {
        // Create group
        post("/group") {
            val group = call.receive<Group>()
            val id = groupService.create(group)
            call.respond(HttpStatusCode.Created, id)
        }
        // List all group
        get("/group") {
            val listTeam = groupService.list()
            call.respond(HttpStatusCode.OK, listTeam)
        }
        // Read group
        get("/group/{id}") {
            val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException("Invalid ID")
            try {
                val group = groupService.read(id)
                call.respond(HttpStatusCode.OK, group)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.NotFound)
            }
        }
        // Update group
        put("/group/{id}") {
            val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException("Invalid ID")
            val group = call.receive<Group>()
            groupService.update(id, group)
            call.respond(HttpStatusCode.OK)
        }
        // Delete group
        delete("/group/{id}") {
            val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException("Invalid ID")
            groupService.delete(id)
            call.respond(HttpStatusCode.OK)
        }
    }
}