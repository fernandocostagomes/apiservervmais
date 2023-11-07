package fernandocostagomes.routes

import fernandocostagomes.schemas.User
import fernandocostagomes.schemas.ServiceUser
import io.ktor.client.plugins.cache.storage.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

fun Application.configureRoutingUser(serviceUser: ServiceUser){

    routing {
        authenticate( "auth-jwt") {

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
                    // Array com os nomes dos arquivos da raiz do projeto.
                    val rootPath = System.getProperty("user.dir")
                    val files = File(rootPath).list()
                    var name = ""
                    for (file in files!!) {
                        name += file + "\n"
                    }
                    val scriptPath = "home/apiservervmais/server_app_update.sh"

                    // Executar o script shell
                    val process = Runtime.getRuntime().exec("sh $scriptPath")

                    // Ler a saída do processo (opcional)
                    val reader = BufferedReader(InputStreamReader(process.inputStream))
                    var line: String? = reader.readLine()
                    var script = ""
                    while (line != null) {
                        // Faça algo com a saída do script, se necessário
                        line = reader.readLine()
                        script += line
                    }

                    // Aguarde o término do processo
                    val exitCode = process.waitFor()

                    // Verifique o código de saída do script
                    if (exitCode == 0) {
                        call.respond(HttpStatusCode.OK, "Script executado com sucesso.")
                    } else {
                        call.respond(HttpStatusCode.InternalServerError, "Erro ao executar o script." +
                                "\n$script" + "\n$name")
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

        post("/createUser") {
            //cria os primeiros usuários.
            val user = User(
                userId = 0,
                userEmail = "admin@admin",
                userName = "admin",
                userPhone = "999999999",
                userNick = "admin",
                userBirthday = "01/01/2000",
                userDate = "01/01/2000",
                userPwdCurrent = "admin123",
                userPwdId = 0
            )

            val id = serviceUser.create( user )
            call.respond(HttpStatusCode.Created, id)
        }
    }
}