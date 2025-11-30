package org.example.application.usecase

import kotlinx.coroutines.delay
import org.example.application.events.CreateOrderEvent
import org.example.application.events.PaymentProcessedEvent
import org.example.application.port.MessagePublisher
import org.example.application.port.PaymentRepository
import org.example.domain.Payment
import org.example.domain.PaymentStatus

class ProcessPayment(
    private val publisher: MessagePublisher,
    private val paymentRepository: PaymentRepository
) {
    private val delayTimeInMs = listOf(15000L)
    private val errorChance = 90
    private val mockedReasons = listOf(
        "saldo insuficiente",
        "fraude suspeita",
        "limite excedido",
        "cartão expirado"
    )

    suspend fun execute(event: CreateOrderEvent, headers: Map<String, Any?>) {
        val delayTime = delayTimeInMs.random()

        delay(delayTime)

        if (shouldFail()) {
            println("Houve falha na processamento do pedido ${event.orderId}")
            val payment = Payment.create(
                orderId = event.orderId,
                amount = event.totalAmount,
                status = PaymentStatus.REJECTED,
                currency = event.currency,
                reason = mockedReasons.random()
            )
            val event = PaymentProcessedEvent(
                paymentId = payment.paymentId,
                orderId = event.orderId,
                status = payment.status,
                processedAt = payment.processedAt,
                reason = payment.reason
            )
            publisher.publish(
                exchange = "payment",
                routingKey = "payment.rejected",
                payload = event,
                serializer = PaymentProcessedEvent.serializer()
            )
            paymentRepository.save(payment)
        } else {
            println("Pedido ${event.orderId} processado com sucesso após ${delayTime}ms")
            val payment = Payment.create(
                orderId = event.orderId,
                amount = event.totalAmount,
                status = PaymentStatus.APPROVED,
                currency = event.currency,
                reason = null
            )
            val event = PaymentProcessedEvent(
                paymentId = payment.paymentId,
                orderId = event.orderId,
                status = payment.status,
                processedAt = payment.processedAt,
                reason = payment.reason
            )
            publisher.publish(
                exchange = "payment",
                routingKey = "payment.approved",
                payload = event,
                serializer = PaymentProcessedEvent.serializer()
            )
            paymentRepository.save(payment)
        }
    }

    private fun shouldFail(): Boolean {
        val value = (0..100).random()
        return value < errorChance
    }
}