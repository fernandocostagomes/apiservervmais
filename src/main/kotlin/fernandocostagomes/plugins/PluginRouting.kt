package fernandocostagomes.plugins

import fernandocostagomes.routes.invalidConst
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.joda.time.LocalDateTime
import java.io.BufferedReader
import java.io.InputStreamReader

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("ApiServer V+ atualizada!\n" +
                    //pega a data e hora.
                   "Data e hora: ${LocalDateTime.now()}\n")
        }
        // Rota que aciona o script server_app_update.sh que esta na raiz do projeto para atualizar o servidor,
        // solicita uma senha para confirmar.

    }
}
