
val ktorVersion: String by project
val kotlinVersion: String by project
val logbackVersion: String by project

val postgresVersion: String by project
val h2Version: String by project
val swaggerCodegenVersion: String by project

plugins {
    kotlin("jvm") version "1.9.0"
    id("io.ktor.plugin") version "2.3.3"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.0"
    id("org.gretty") version "4.0.3"
    id("war")
}

group = "fernandocostagomes"
version = "0.0.1"

application {
    mainClass.set("fernandocostagomes.ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
}

gretty {
    servletContainer = "jetty11"
    contextPath = "/"
    logbackConfigFile = "src/main/resources/logback.xml"
}

afterEvaluate {
    tasks.getByName("run") {
        dependsOn("appRun")
    }
}

dependencies {
    implementation("io.ktor:ktor-server-core-jvm")
    implementation("io.ktor:ktor-server-websockets-jvm")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm")
    implementation("io.ktor:ktor-server-content-negotiation-jvm")
    implementation("org.postgresql:postgresql:$postgresVersion")
    implementation("io.ktor:ktor-serialization-gson-jvm")
    implementation("io.ktor:ktor-server-cors-jvm")
    implementation("io.ktor:ktor-server-openapi")
    implementation("io.ktor:ktor-server-swagger")
    implementation("io.ktor:ktor-server-http-redirect-jvm")
    implementation("io.ktor:ktor-server-host-common-jvm")
    implementation("io.ktor:ktor-server-resources")
    implementation("io.ktor:ktor-server-auto-head-response-jvm")
    implementation("io.ktor:ktor-server-sessions-jvm")
    implementation("io.ktor:ktor-server-auth-jvm")
    implementation("io.ktor:ktor-server-auth-jwt-jvm")
    implementation("io.ktor:ktor-server-netty-jvm")
    implementation("io.swagger.codegen.v3:swagger-codegen-generators:$swaggerCodegenVersion")
    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    implementation("io.ktor:ktor-server-servlet-jakarta:$ktorVersion")

    testImplementation("io.ktor:ktor-server-tests-jvm")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlinVersion")
}
