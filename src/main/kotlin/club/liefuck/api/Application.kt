package club.liefuck.api

import club.liefuck.api.data.DatabaseFactory
import club.liefuck.api.plugins.configureHTTP
import club.liefuck.api.plugins.configureMonitoring
import club.liefuck.api.plugins.configureRouting
import club.liefuck.api.plugins.configureSerialization
import com.typesafe.config.ConfigFactory
import io.ktor.config.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory

val appConfig = HoconApplicationConfig(ConfigFactory.load())
val IS_PROD = appConfig.property("ktor.environment").getString() == "prod"
val logger: Logger = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME)

fun main() {
    embeddedServer(CIO, port = 9090, host = "0.0.0.0") {
        configureRouting()
        configureHTTP()
        configureMonitoring()
        configureSerialization()
        DatabaseFactory.init(appConfig)
    }.start(wait = true)
}
