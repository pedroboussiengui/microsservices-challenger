package org.example.infra.http

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.plugins.NotFoundException
import io.ktor.server.plugins.requestvalidation.RequestValidationException
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respond
import kotlinx.serialization.Serializable

// RFC 5497
@Serializable
data class ProblemDetails(
    val title: String,
    val status: Int,
    val detail: String,
    val errors: List<String>? = null
)

fun Application.configureStatusPages() {
    install(StatusPages) {
        exception<BadRequestException> { call, cause ->
            call.respond(
                HttpStatusCode.BadRequest,
                ProblemDetails(
                    title = "Serialization Error",
                    status = HttpStatusCode.BadRequest.value,
                    detail = cause.message ?: "Unknown serialization error",
                )
            )
        }
        exception<RequestValidationException> { call, cause ->
            call.respond(
                HttpStatusCode.BadRequest,
                ProblemDetails(
                    title = "Validation Error",
                    status = HttpStatusCode.BadRequest.value,
                    detail = "Invalid request",
                    errors = cause.reasons
                )
            )
        }
        exception<NotFoundException> { call, cause ->
            call.respond(
                HttpStatusCode.NotFound,
                ProblemDetails(
                    title = "Not Found",
                    status = HttpStatusCode.NotFound.value,
                    detail = cause.message ?: "Resource not found"
                )
            )
        }
    }
}