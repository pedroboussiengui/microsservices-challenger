package org.example

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.calllogging.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.example.infra.di.configureDI
import org.example.infra.di.errorUseCase
import org.example.infra.di.successUseCase
import org.slf4j.event.Level

fun Application.routes() {
    install(CallLogging) {
        level = Level.INFO
        filter { call -> true }
    }
    configureDI()
    routing {
        get("/success") {
            val service = application.successUseCase
            val response = service.execute()
            call.respondText(response)
        }
        get("/error") {
            val service = application.errorUseCase
            service.execute()
        }
    }
}

fun main() {
    embeddedServer(Netty, port = 8083) {
        routes()
    }.start(wait = true)
}