package fernandocostagomes.plugins

import fernandocostagomes.routes.invalidConst
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.io.BufferedReader
import java.io.InputStreamReader

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("ApiServer V+ atualizada!")
        }
        // Rota que aciona o script server_app_update.sh que esta na raiz do projeto para atualizar o servidor,
        // solicita uma senha para confirmar.
        get("/update/{pwd}") {
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
