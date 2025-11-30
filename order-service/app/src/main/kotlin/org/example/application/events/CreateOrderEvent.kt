package org.example.application.events

import kotlinx.serialization.Serializable
import org.example.application.usecase.OrderItemDto
import org.example.domain.CurrenyType
import org.example.domain.Order
import org.example.infra.http.UUIDSerializer
import java.util.UUID

fun Order.toEvent() = CreateOrderEvent(
    id = id,
    requestId = requestId,
    items = items.map {
        OrderItemDto(
            it.productId,
            it.quantity,
            it.unitPrice) },
    currency = currency
)

@Serializable
data class CreateOrderEvent(
    @Serializable(with = UUIDSerializer::class)
    val id: UUID,
    @Serializable(with = UUIDSerializer::class)
    val requestId: UUID,
    val items: List<OrderItemDto>,
    val currency: CurrenyType
)