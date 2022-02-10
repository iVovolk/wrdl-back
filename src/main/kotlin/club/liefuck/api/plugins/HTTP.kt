package club.liefuck.api.plugins

import club.liefuck.api.IS_PROD
import club.liefuck.api.appConfig
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import java.time.Duration

fun Application.configureHTTP() {
    install(CORS) {
        method(HttpMethod.Options)
        header(HttpHeaders.ContentType)
        header(HttpHeaders.AcceptEncoding)
        header(HttpHeaders.AcceptLanguage)
        header(HttpHeaders.AccessControlRequestMethod)
        header(HttpHeaders.AccessControlRequestHeaders)
        header(HttpHeaders.Authorization)
        exposeHeader(HttpHeaders.AccessControlAllowOrigin)
        exposeHeader(HttpHeaders.AccessControlAllowHeaders)
        exposeHeader(HttpHeaders.AccessControlAllowMethods)
        exposeHeader(HttpHeaders.StrictTransportSecurity)

        maxAgeInSeconds = Duration.ofDays(1).seconds
        allowNonSimpleContentTypes = true

        if (!IS_PROD) {
            anyHost()
        } else {
            allowCredentials = true
            host(appConfig.property("ktor.httpHost").getString(), schemes = listOf("https"))
        }
    }
    install(DefaultHeaders)

    if (IS_PROD) {
        install(ForwardedHeaderSupport)
        install(XForwardedHeaderSupport)
    }

    install(HSTS) {
        includeSubDomains = true
    }
}
