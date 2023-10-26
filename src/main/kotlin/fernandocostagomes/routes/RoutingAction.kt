package fernandocostagomes.routes

import fernandocostagomes.schemas.Action
import fernandocostagomes.schemas.ServiceAction
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.io.BufferedReader
import java.io.InputStreamReader

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

        get( actionConst + "/update/{pwd}" ) {
            val password = call.parameters["pwd"] ?: throw IllegalArgumentException(invalidConst)
            val senha = "update"
            if (password != senha) {
                call.respond("Pwd inválida.")
            }

            try {
                // Caminho para o script shell na raiz do projeto
                val scriptPath = "./server_app_update.sh"

                // Executar o script shell
                val process = Runtime.getRuntime().exec("sh $scriptPath")

                // Ler a saída do processo (opcional)
                val reader = BufferedReader(InputStreamReader(process.inputStream))
                var line: String? = reader.readLine()
                while (line != null) {
                    // Faça algo com a saída do script, se necessário
                    line = reader.readLine()
                }

                // Aguarde o término do processo
                val exitCode = process.waitFor()

                // Verifique o código de saída do script
                if (exitCode == 0) {
                    call.respond(HttpStatusCode.OK, "Script executado com sucesso.")
                } else {
                    call.respond(HttpStatusCode.InternalServerError, "Erro ao executar o script.")
                }
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, "Erro ao executar o script: ${e.message}")
            }
        }
    }
}