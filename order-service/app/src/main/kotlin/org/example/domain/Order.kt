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

    fun confirmOrder() = applyStatusTransition(OrderStatus.CONFIRMED)

    fun failOrder() = applyStatusTransition(OrderStatus.FAILED)

    fun cancelOrder() = applyStatusTransition(OrderStatus.CANCELLED)

    private fun applyStatusTransition(newStatus: OrderStatus) {
        status = newStatus
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
