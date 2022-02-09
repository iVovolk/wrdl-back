package club.liefuck.api.plugins

import club.liefuck.api.IS_PROD
import club.liefuck.api.logger
import club.liefuck.api.service.Config
import club.liefuck.api.service.StatsService
import club.liefuck.api.service.UserService
import club.liefuck.api.service.WordService
import club.liefuck.api.storage.Mode
import club.liefuck.api.storage.Size
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.util.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException

fun Application.configureRouting() {
    install(AutoHeadResponse)


    routing {
        post("/words/check") {
            val wr = call.receive<WordCheckRequest>()
            val userId = UserService.findIdByKey(wr.userKey) ?: run {
                call.respond(HttpStatusCode.BadRequest, "Неизвестный пользователь")
                return@post
            }
            WordService.check(wr, userId)?.let {
                call.respond(it)
            } ?: run {
                call.respond(HttpStatusCode.NotFound, "Не знаю такого слова")
            }
        }

        post("/words/check/last-turn") {
            val wr = call.receive<WordCheckRequest>()
            val userId = UserService.findIdByKey(wr.userKey) ?: run {
                call.respond(HttpStatusCode.BadRequest, "Неизвестный пользователь")
                return@post
            }
            WordService.check(wr, userId, true)?.let {
                call.respond(it)
            } ?: run {
                call.respond(HttpStatusCode.NotFound, "Не знаю такого слова")
            }
        }

        post("/stats") {
            val sr = call.receive<StatsRequest>()
            val userId = UserService.findIdByKey(sr.userKey) ?: run {
                call.respond(HttpStatusCode.BadRequest, "Неизвестный пользователь")
                return@post
            }
            call.respond(StatsService.load(Config(userId, sr.size, sr.mode)))
        }

        get("/user/new") {
            call.respond(UserService.addNew())
        }

        get("/user/{key}/check") {
            call.parameters["key"]?.let { key ->
                UserService.withStats(key)?.let {
                    call.respond(it)
                } ?: run {
                    call.respond(HttpStatusCode.NotFound)
                }
            } ?: call.respond(HttpStatusCode.NotFound)
        }

        install(StatusPages) {
            //response conversion errors
            exception<SerializationException> { c->
                call.respond(HttpStatusCode.BadRequest, c.localizedMessage)
            }
            //general logic errors
            exception<IllegalStateException> { cause ->
                call.respond(HttpStatusCode.BadRequest, cause.localizedMessage)
            }
            //request validation errors
            exception<IllegalArgumentException> { cause ->
                call.respond(HttpStatusCode.BadRequest, cause.localizedMessage)
            }
            //server errors
            exception<RuntimeException> { cause ->
                call.respond(HttpStatusCode.InternalServerError, "Что-то сломалось на сервере")
                logger.error(cause)
            }
            //handler for prod only to prevent exposure of internals in case of unhandled failure
            if (IS_PROD) {
                exception<Throwable> { cause ->
                    call.respond(HttpStatusCode.InternalServerError, "Что-то сломалось на сервере")
                    logger.error(cause)
                }
            }

        }
    }
}

@Serializable
data class WordCheckRequest(val userKey: String, val mode: Byte, val size: Byte, val word: String) {
    init {
        require(userKey.isNotBlank() && userKey.length == UserService.KEY_LENGTH) {
            "Ключ пользователя пустой или неправильный"
        }
        requireNotNull(enumValues<Mode>().find { it.mode == mode }) {
            "Неизвестный режим сложности"
        }
        requireNotNull(enumValues<Size>().find { it.size == size }) {
            "Неверная длина слова"
        }
        require(word.isNotBlank() && word.length == size.toInt()) {
            "Слово пустое или неверной длины"
        }
    }
}

@Serializable
data class StatsRequest(val userKey: String, val mode: Byte, val size: Byte) {
    init {
        require(userKey.isNotBlank() && userKey.length == UserService.KEY_LENGTH) {
            "Ключ пользователя пустой или неправильный"
        }
        requireNotNull(enumValues<Mode>().find { it.mode == mode }) {
            "Неизвестный режим сложности"
        }
        requireNotNull(enumValues<Size>().find { it.size == size }) {
            "Неверная длина слова"
        }
    }
}
