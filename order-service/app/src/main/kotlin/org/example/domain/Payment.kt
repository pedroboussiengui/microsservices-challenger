package org.example.domain

import java.time.Instant
import java.util.*

class Payment(
    val payment: UUID,
    val paymentStatus: PaymentStatus,
    val processedAt: Instant
) {
}

enum class PaymentStatus {
    PENDING,
    APPROVED,
    REJECTED
}