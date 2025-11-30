package org.example.application.usecase

import org.example.application.events.PaymentProcessedEvent
import org.example.infra.database.OrderRepository

class UpdateOrder(
    private val orderRepository: OrderRepository
) {
    suspend fun execute(event: PaymentProcessedEvent, headers: Map<String, Any?>) {
        println("orderId: ${event.orderId}")
        println("paymentId: ${event.paymentId}")
        println("status: ${event.status}")
        println("processedAt: ${event.processedAt}")
        println("reason: ${event.reason}")
    }
}