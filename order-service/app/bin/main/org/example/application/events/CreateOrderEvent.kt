package org.example.application.events

import kotlinx.serialization.Serializable
import org.example.domain.CurrenyType
import org.example.domain.Order
import org.example.infra.http.InstantSerializer
import org.example.infra.http.UUIDSerializer
import java.time.Instant
import java.util.*

fun Order.toEvent() = CreateOrderEvent(
    eventType = "OrderCreated",
    orderId = id,
    requestId = requestId,
    customerId = customerId,
    totalAmount = totalAmount,
    currency = currency,
    timestamp = Instant.now()
)

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