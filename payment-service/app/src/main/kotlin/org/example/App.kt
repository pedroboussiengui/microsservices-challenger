package org.example

import io.ktor.server.application.*
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import org.example.infra.http.paymentModule

fun Application.module() {
    paymentModule()
}

fun main() {
    embeddedServer(Netty, port = 8082, module = Application::module)
        .start(wait = true)
}
