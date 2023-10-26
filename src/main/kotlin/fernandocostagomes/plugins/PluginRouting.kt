package fernandocostagomes.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.joda.time.LocalDateTime

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("ApiServer V+ atualizada!\n" +
                    //pega a data e hora.
                   "Data e hora: ${LocalDateTime.now()}\n" +
                //pega o head do commit
                     "Head do commit: ${System.getenv("HEAD_COMMIT")}\n" )
        }

        get("update"){
            try {
                // Carregue o script da pasta "resources"
                val scriptStream = this::class.java.classLoader.getResourceAsStream("server_app_update.sh")

                if (scriptStream != null) {
                    // Crie um arquivo temporário para o script
                    val scriptFile = createTempFile()
                    scriptFile.writeBytes(scriptStream.readAllBytes())

                    // Dê permissão de execução ao arquivo temporário
                    scriptFile.setExecutable(true)

                    // Execute o script shell
                    val process = Runtime.getRuntime().exec(scriptFile.absolutePath)

                    // ... (resto do código para leitura da saída e verificação do código de saída)

                    scriptFile.delete() // Lembre-se de excluir o arquivo temporário após a execução
                } else {
                    call.respond(HttpStatusCode.InternalServerError, "Script não encontrado.")
                }
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, "Erro ao executar o script: ${e.message}")
            }

        }
        // Rota que aciona o script server_app_update.sh que esta na raiz do projeto para atualizar o servidor,
        // solicita uma senha para confirmar.

    }
}
