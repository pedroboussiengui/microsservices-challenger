package org.example.infra.http

import com.rabbitmq.client.ConnectionFactory
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.plugins.requestvalidation.RequestValidation
import io.ktor.server.plugins.requestvalidation.ValidationResult
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.example.application.usecase.CreateOrder
import org.example.application.usecase.CreateOrderRequest
import org.example.application.usecase.OrderItemDto
import org.example.application.usecase.RetrieveOrderById
import org.example.infra.database.PostgresOrderRepository
import org.example.infra.messaging.RabbitMessagePublisher
import org.valiktor.ConstraintViolationException
import org.valiktor.functions.isNotEmpty
import org.valiktor.functions.isNotNull
import org.valiktor.functions.isPositive
import org.valiktor.functions.validateForEach
import org.valiktor.validate
import java.util.UUID


fun CreateOrderRequest.validate() {
    validate(this) {
        validate(CreateOrderRequest::requestId)
            .isNotNull()
        validate(CreateOrderRequest::items)
            .isNotEmpty()
        validate(CreateOrderRequest::items).validateForEach {
            validate(OrderItemDto::productId)
                .isNotNull()
            validate(OrderItemDto::quantity)
                .isNotNull()
                .isPositive()
            validate(OrderItemDto::unitPrice)
                .isNotNull()
                .isPositive()
        }
        validate(CreateOrderRequest::currency)
            .isNotNull()
    }
}

fun Application.orderModule() {

    install(RequestValidation) {
        validate<CreateOrderRequest> { request ->
            try {
                request.validate()
                ValidationResult.Valid
            } catch (e: ConstraintViolationException) {
                ValidationResult.Invalid(validationErrors(e))
            }
        }
    }

    val factory = ConnectionFactory().apply {
        host = "localhost"
        port = 5672
        username = "guest"
        password = "guest"
    }
    val connection = factory.newConnection()
    val publisher = RabbitMessagePublisher(connection)

    val orderRepository = PostgresOrderRepository()
    val createOrderUseCase = CreateOrder(orderRepository, publisher)
    val retrieveOrderById = RetrieveOrderById(orderRepository)

    routing {
        route("/internal/orders") {
            post {
                val request = call.receive<CreateOrderRequest>()
                val customerId = call.request.headers["x-customer-id"]
                    ?: throw BadRequestException("Customer ID header is missing")
                val response = createOrderUseCase.execute(request, UUID.fromString(customerId))
                call.respond(HttpStatusCode.Created, response)
            }
            get("/{orderId}") {
                val orderId = call.parameters.uuid("orderId")
                    ?: throw BadRequestException("Order ID must be a valid UUID")
                val response = retrieveOrderById.execute(orderId)
                call.respond(HttpStatusCode.OK, response)
            }
        }
    }
}