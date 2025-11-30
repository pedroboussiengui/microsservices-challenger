package org.example.application.usecase

import org.example.application.events.PaymentProcessedEvent
import org.example.application.events.PaymentStatus
import org.example.infra.database.OrderRepository

class UpdateOrder(
    private val orderRepository: OrderRepository
) {
    suspend fun execute(event: PaymentProcessedEvent, headers: Map<String, Any?>) {
        val order = orderRepository.findById(event.orderId)
            ?: throw IllegalArgumentException("Order not found")

        when(event.status) {
            PaymentStatus.APPROVED -> order.confirmOrder(event.paymentId, event.processedAt)
            PaymentStatus.REJECTED -> order.failOrder(event.paymentId, event.processedAt)
        }
        println("Updating product payment status")
        orderRepository.update(order)
    }
}