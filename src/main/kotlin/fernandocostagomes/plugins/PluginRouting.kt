package fernandocostagomes.plugins

import fernandocostagomes.schemas.ServiceUser
import fernandocostagomes.schemas.User
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.joda.time.LocalDateTime
import java.sql.Connection

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText(
                "ApiServer V+ atualizada!\n" +
                        //pega a data e hora.
                        "Data e hora: ${LocalDateTime.now()}\n" +
                        //pega o head do commit
                        "Head do commit: ${System.getenv("HEAD_COMMIT")}\n"
            )
        }
    }
}
