package org.example.domain

import java.time.Instant
import java.util.UUID

class Payment(
    val paymentId: UUID,
    val orderId: UUID,
    val status: PaymentStatus,
    val amount: Double,
    val currency: CurrenyType,
    val reason: String?,
    val processedAt: Instant
) {
    companion object {
        fun create(
            orderId: UUID,
            amount: Double,
            status: PaymentStatus,
            currency: CurrenyType,
            reason: String?
        ): Payment {
            return Payment(
                paymentId = UUID.randomUUID(),
                orderId = orderId,
                status = status,
                amount = amount,
                currency = currency,
                reason = reason,
                processedAt = Instant.now()
            )
        }
    }
}

enum class PaymentStatus {
    APPROVED, REJECTED
}

enum class CurrenyType {
    BRL, EUR, USD
}