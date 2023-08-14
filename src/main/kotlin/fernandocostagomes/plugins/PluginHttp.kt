package fernandocostagomes.plugins

import io.ktor.http.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.openapi.*
import io.ktor.server.routing.*
import io.ktor.server.plugins.swagger.*
import io.ktor.server.application.*
import io.swagger.codegen.v3.generators.html.StaticHtmlCodegen

fun Application.configureHTTP() {

    install(CORS) {
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
        allowMethod(HttpMethod.Patch)
        allowHeader(HttpHeaders.Authorization)
        allowHeader("MyCustomHeader")
        anyHost()
    }

    routing {
        openAPI(path = "openapi"){
            codegen = StaticHtmlCodegen()
        }
    }

    routing {
        swaggerUI(path = "openapi")
    }
}
