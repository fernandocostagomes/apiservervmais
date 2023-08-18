package fernandocostagomes.plugins

import io.ktor.http.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.openapi.*
import io.ktor.server.routing.*
import io.ktor.server.plugins.swagger.*
import io.ktor.server.application.*
import io.swagger.codegen.v3.generators.html.StaticHtmlCodegen
import io.swagger.v3.oas.models.OpenAPI

fun Application.configureHTTP() {

    install(CORS) {
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
        allowMethod(HttpMethod.Patch)
        allowHeader(HttpHeaders.Authorization)
        allowHeader(HttpHeaders.ContentType)
        allowHeader("MyCustomHeader")
        anyHost()
    }

    routing {
        openAPI(path = "openapi")
    }

    routing {
        swaggerUI(path = "openapi")
    }
}
