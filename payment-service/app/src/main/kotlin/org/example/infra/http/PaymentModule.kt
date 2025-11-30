package org.example.infra.http

import com.rabbitmq.client.ConnectionFactory
import io.ktor.server.application.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.example.application.events.CreateOrderEvent
import org.example.application.usecase.ProcessPayment
import org.example.infra.database.PostgresPaymentRepository
import org.example.infra.messaging.RabbitMessageConsumer
import org.example.infra.messaging.RabbitMessagePublisher

fun Application.paymentModule() {
    val factory = ConnectionFactory().apply {
        host = "localhost"
        port = 5672
        username = "guest"
        password = "guest"
    }
    val connection = factory.newConnection()
    val consumer = RabbitMessageConsumer(connection)
    val publisher = RabbitMessagePublisher(connection)
    val paymentRepository = PostgresPaymentRepository()

    val processPayment = ProcessPayment(publisher, paymentRepository)

    CoroutineScope(Dispatchers.IO).launch {
        consumer.consume(
            queue = "order.created.queue",
            serializer = CreateOrderEvent.serializer()
        ) { event, headers ->
            processPayment.execute(event, headers)
        }
    }
}