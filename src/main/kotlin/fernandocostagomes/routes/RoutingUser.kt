package fernandocostagomes.routes

import fernandocostagomes.schemas.User
import fernandocostagomes.schemas.ServiceUser
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.io.BufferedReader
import java.io.InputStreamReader

fun Application.configureRoutingUser(serviceUser: ServiceUser){

    routing {

        // Create user
        post( userConst ) {
            val user = call.receive<User>()
            val id = serviceUser.create(user)
            call.respond(HttpStatusCode.Created, id)
        }

        // Delete user
        delete( userMoreIdConst ) {
            val id = call.parameters[ idConst ]?.toInt() ?: throw IllegalArgumentException( invalidConst )
            serviceUser.delete(id)
            call.respond(HttpStatusCode.OK)
        }

        // List all user
        get( userConst ) {
            val listUser = serviceUser.list()
            call.respond(HttpStatusCode.OK, listUser)
        }

        // Read user
        get( userMoreIdConst ) {
            val id = call.parameters[ idConst ]?.toInt() ?: throw IllegalArgumentException( invalidConst )
            try {
                val user = serviceUser.read(id)
                call.respond(HttpStatusCode.OK, user)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.NotFound)
            }
        }

        // Read user for email
        get( userConst + userEmail ) {
            val email = call.parameters[ emailConst ]?: throw IllegalArgumentException( invalidConst )
            try {
                val user = serviceUser.read(email)
                call.respond(HttpStatusCode.OK, user)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.NotFound)
            }
        }

        get( userConst + userUpdate ) {
            val password = call.parameters[ "pwd" ]?: throw IllegalArgumentException( invalidConst )
            val senha = "update"
            if (password != senha) {
                call.respond(HttpStatusCode.OK,"Pwd inválida.")
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

        // Update user
        put( userMoreIdConst ) {
            val id = call.parameters[ idConst ]?.toInt() ?: throw IllegalArgumentException( invalidConst )
            val user = call.receive<User>()
            serviceUser.update(id, user)
            call.respond(HttpStatusCode.OK)
        }
    }
}