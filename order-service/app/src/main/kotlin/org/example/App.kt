package org.example

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import kotlinx.serialization.json.Json
import org.example.infra.database.OrderItemModel
import org.example.infra.database.OrderModel
import org.example.infra.di.configureDI
import org.example.infra.http.configureStatusPages
import org.example.infra.http.orderModule
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

fun Application.module() {
    install(ContentNegotiation) {
        json(Json {
            explicitNulls = false
        })
    }
    configureDI()
    configureStatusPages()
    orderModule()
    transaction {
        exec("CREATE SCHEMA IF NOT EXISTS order_service;")
        SchemaUtils.drop(OrderItemModel, OrderModel)
        SchemaUtils.create(OrderModel, OrderItemModel)
    }
}

fun main() {

    embeddedServer(Netty, port = 8080, module = Application::module)
        .start(wait = true)
}