package fernandocostagomes.utils

import fernandocostagomes.services.secret_variables.SecretVariableName
import fernandocostagomes.services.secret_variables.SecretVariablesService
import java.io.File

fun isProductionServer(): Boolean = SecretVariablesService.get(SecretVariableName.ProductionServer, "false").toBoolean()
fun getUserWorkingDirectory(): String {
    if (!isProductionServer()) {
        return File(".").canonicalPath
    }
    return File(object {}.javaClass.protectionDomain.codeSource.location.toURI().path).parent
}