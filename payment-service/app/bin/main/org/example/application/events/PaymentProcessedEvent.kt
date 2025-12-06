package org.example.application.events

import kotlinx.serialization.Serializable
import org.example.domain.PaymentStatus
import org.example.infra.http.InstantSerializer
import org.example.infra.http.UUIDSerializer
import java.time.Instant
import java.util.UUID

@Serializable
data class PaymentProcessedEvent(
    @Serializable(with = UUIDSerializer::class)
    val paymentId: UUID,
    @Serializable(with = UUIDSerializer::class)
    val orderId: UUID,
    val status: PaymentStatus,
    @Serializable(with = InstantSerializer::class)
    val processedAt: Instant,
    val reason: String?
)