package org.example.application.events

import kotlinx.serialization.Serializable
import org.example.infra.http.InstantSerializer
import org.example.infra.http.UUIDSerializer
import java.time.Instant
import java.util.UUID

@Serializable
data class CreateOrderEvent(
    val eventType: String,
    @Serializable(with = UUIDSerializer::class)
    val orderId: UUID,
    @Serializable(with = UUIDSerializer::class)
    val requestId: UUID,
    @Serializable(with = UUIDSerializer::class)
    val customerId: UUID,
    val totalAmount: Double,
    val currency: CurrenyType,
    @Serializable(with = InstantSerializer::class)
    val timestamp: Instant
)

enum class CurrenyType {
    BRL,
    EUR,
    USD
}