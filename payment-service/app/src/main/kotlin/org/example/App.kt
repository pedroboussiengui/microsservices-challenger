package org.example

import io.ktor.server.application.*
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import org.example.infra.database.DatabaseConnection
import org.example.infra.database.PaymentModel
import org.example.infra.http.paymentModule
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

fun Application.module() {
    paymentModule()
}

fun main() {
    DatabaseConnection.init()

    transaction {
        exec("CREATE SCHEMA IF NOT EXISTS payment_service;")
        SchemaUtils.create(PaymentModel)
    }

    embeddedServer(Netty, port = 8082, module = Application::module)
        .start(wait = true)
}
