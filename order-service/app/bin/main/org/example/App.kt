package org.example

import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.*
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.opentelemetry.exporter.otlp.trace.OtlpGrpcSpanExporter
import io.opentelemetry.sdk.OpenTelemetrySdk
import io.opentelemetry.sdk.trace.SdkTracerProvider
import io.opentelemetry.sdk.trace.export.BatchSpanProcessor
import kotlinx.serialization.json.Json
import org.example.infra.database.DatabaseConnection
import org.example.infra.database.OrderItemModel
import org.example.infra.database.OrderModel
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
    DatabaseConnection.init(
        "jdbc:postgresql://localhost:5432/microservices",
        "postgres",
        "postgres"
    )
    transaction {
        exec("CREATE SCHEMA IF NOT EXISTS order_service;")
        SchemaUtils.drop(OrderItemModel, OrderModel)
        SchemaUtils.create(OrderModel, OrderItemModel)
    }

    configureStatusPages()
    orderModule()

//    monitor.subscribe(ApplicationStopped) { application ->
//        application.environment.log.info("Server is stopped")
//        // Release resources and unsubscribe from events
//        monitor.unsubscribe(ApplicationStarted) {}
//        monitor.unsubscribe(ApplicationStopped) {}
//    }
}

fun main() {

    embeddedServer(Netty, port = 8080, module = Application::module)
        .start(wait = true)
}