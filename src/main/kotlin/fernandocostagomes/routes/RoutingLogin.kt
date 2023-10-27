package fernandocostagomes.routes

import fernandocostagomes.schemas.ServicePwd
import fernandocostagomes.schemas.ServiceUser
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRoutingLogin(serviceUser: ServiceUser, servicePwd: ServicePwd) {
    class Login(val email: String, val password: String)

    routing {

        // Create action
        post( loginConst ) {
            val login = call.receive<Login>()

            //Busca o email e verifica se tem no banco de dados.
            val user = serviceUser.read( login.email )

            //Busca a senha e verifica se tem no banco de dados.
            val pwd = servicePwd.read( user.userId )

            if(user == null)
                call.respond(HttpStatusCode.OK, "User not found")

            if(pwd.currentPwd == login.password){
                call.respond(HttpStatusCode.OK, "Login successful")
            }
            else{
                call.respond(HttpStatusCode.NotFound, "Password incorrect")
            }
        }
    }
}