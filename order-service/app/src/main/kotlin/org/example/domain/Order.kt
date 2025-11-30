package org.example.domain

import java.time.Instant
import java.util.*

class Order(
    val id: UUID,
    val requestId: UUID,
    val customerId: UUID,
    val items: List<Item>,
    val totalAmount: Double,
    val currency: CurrenyType,
    var status: OrderStatus,
    var createdAt: Instant,
    var updatedAt: Instant?,
    var payment: Payment?
) {
    companion object {
        fun create(
            requestId: UUID,
            customerId: UUID,
            items: List<Item>,
            currency: CurrenyType
        ): Order {
            return Order(
                UUID.randomUUID(),
                requestId,
                customerId,
                items,
                items.sumOf { it.quantity * it.unitPrice },
                currency,
                OrderStatus.PENDING,
                Instant.now(),
                null,
                null
            )
        }
    }

    fun confirmOrder(paymentId: UUID, processedAt: Instant) {
        status = OrderStatus.CONFIRMED
        updatedAt = Instant.now()
        payment = Payment(paymentId, PaymentStatus.APPROVED, processedAt)
    }

    fun failOrder(paymentId: UUID, processedAt: Instant) {
        status = OrderStatus.FAILED
        updatedAt = Instant.now()
        payment = Payment(paymentId, PaymentStatus.REJECTED, processedAt)
    }

    fun cancelOrder() {
        status = OrderStatus.CANCELLED
        updatedAt = Instant.now()
    }
}


enum class OrderStatus {
    PENDING,
    CONFIRMED,
    FAILED,
    CANCELLED
}

enum class CurrenyType {
    BRL,
    EUR,
    USD
}
