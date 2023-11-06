package fernandocostagomes.plugins

import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import fernandocostagomes.routes.loginConst
import fernandocostagomes.schemas.ServicePwd
import fernandocostagomes.schemas.ServiceUser
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
class Login(val email: String, val password: String)

fun Application.configureSecurity( ) {

    val secret = environment.config.propertyOrNull("ktor.deployment.secret")?.getString() ?: "senha123senha"
    val issuer = environment.config.propertyOrNull("ktor.deployment.issuer")?.getString() ?: "http://0.0.0.0:8080/"
    val audience = environment.config.propertyOrNull("ktor.deployment.audience")?.getString() ?: "http://0.0.0.0:8080/login"
    val myRealm = environment.config.propertyOrNull("ktor.deployment.realm")?.getString() ?: "Access to the API"

    install( Authentication ){
        jwt("auth-jwt") {
            realm = myRealm
            verifier(
                JWT
                    .require(Algorithm.HMAC256(secret))
                    .withAudience(audience)
                    .withIssuer(issuer)
                    .build()
            )
            validate { credential ->
                if (credential.payload.getClaim("email").asString() != "")
                    JWTPrincipal(credential.payload)
                else
                    null
            }
            challenge { s, _ ->
                call.respond(
                    HttpStatusCode.Unauthorized,
                    "Token is not valid or has expired"
                )
            }
        }
    }
}
