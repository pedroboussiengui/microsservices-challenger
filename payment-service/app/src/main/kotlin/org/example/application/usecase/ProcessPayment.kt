package org.example.application.usecase

import kotlinx.coroutines.delay
import org.example.application.events.CreateOrderEvent
import org.example.application.events.PaymentProcessedEvent
import org.example.application.events.PaymentStatus
import org.example.application.port.MessagePublisher
import java.time.Instant
import java.util.UUID

class ProcessPayment(
    private val publisher: MessagePublisher
) {
    val delayTimeInMs = listOf(15000L)
    val errorChance = 90
    val mockedReasons = listOf(
        "saldo insuficiente",
        "fraude suspeita",
        "limite excedido",
        "cartão expirado"
    )

    suspend fun execute(event: CreateOrderEvent, headers: Map<String, Any?>) {
        val delayTime = delayTimeInMs.random()

        delay(delayTime)

        return if (shouldFail()) {
            println("Houve falha na processamento do pedido ${event.orderId}")
            val event = PaymentProcessedEvent(
                paymentId = UUID.randomUUID(),
                orderId = event.orderId,
                status = PaymentStatus.REJECTED,
                processedAt = Instant.now(),
                reason = mockedReasons.random()
            )
            publisher.publish(
                exchange = "payment",
                routingKey = "payment.rejected",
                payload = event,
                serializer = PaymentProcessedEvent.serializer()
            )
        } else {
            println("Pedido ${event.orderId} processado com sucesso após ${delayTime}ms")
            val event = PaymentProcessedEvent(
                paymentId = UUID.randomUUID(),
                orderId = event.orderId,
                status = PaymentStatus.APPROVED,
                processedAt = Instant.now(),
                reason = null
            )
            publisher.publish(
                exchange = "payment",
                routingKey = "payment.approved",
                payload = event,
                serializer = PaymentProcessedEvent.serializer()
            )
        }
    }

    private fun shouldFail(): Boolean {
        val value = (0..100).random()
        return value < errorChance
    }
}