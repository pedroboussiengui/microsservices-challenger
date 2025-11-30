package org.example.application.usecase

import kotlinx.serialization.Serializable
import org.example.application.port.MessagePublisher
import org.example.domain.*
import org.example.infra.database.OrderRepository
import org.example.infra.http.InstantSerializer
import org.example.infra.http.UUIDSerializer
import org.example.application.events.CreateOrderEvent
import org.example.application.events.toEvent
import java.time.Instant
import java.util.*

class CreateOrder(
    private val orderRepository: OrderRepository,
    private val publisher: MessagePublisher
) {
    suspend fun execute(input: CreateOrderRequest, customerId: UUID): CreteOrderResponse {
        val items = input.items.map {
            Item(
                productId = it.productId,
                quantity = it.quantity,
                unitPrice = it.unitPrice
            )
        }
        val order = Order.create(
            input.requestId,
            customerId,
            items,
            input.currency
        )
        // save order
        orderRepository.save(order)
        // send event
        val event = order.toEvent()
        publisher.publish(
            exchange = "order",
            routingKey = "order.created",
            payload = event,
            serializer = CreateOrderEvent.serializer(),
            headers = mapOf("x-request-id" to order.requestId.toString())
        )
        // prepare response
        val itemsResponse = order.items.map {
            OrderItemDto(
                productId = it.productId,
                quantity = it.quantity,
                unitPrice = it.unitPrice
            )
        }
        val paymentResponse = OrderPaymentDto(
            payment = order.payment.payment,
            paymentStatus = order.payment.paymentStatus,
            processedAt = order.payment.processedAt
        )
        return CreteOrderResponse(
            order.id,
            order.requestId,
            order.customerId,
            itemsResponse,
            order.totalAmount,
            order.currency,
            order.status,
            order.createdAt,
            order.updatedAt,
            paymentResponse
        )
    }
}

@Serializable
data class CreateOrderRequest(
    @Serializable(with = UUIDSerializer::class)
    val requestId: UUID,
    val items: List<OrderItemDto>,
    val currency: CurrenyType
)

@Serializable
data class OrderItemDto(
    @Serializable(with = UUIDSerializer::class)
    val productId: UUID,
    val quantity: Int,
    val unitPrice: Double
)

@Serializable
class OrderPaymentDto(
    @Serializable(with = UUIDSerializer::class)
    val payment: UUID,
    val paymentStatus: PaymentStatus,
    @Serializable(with = InstantSerializer::class)
    val processedAt: Instant
)

@Serializable
data class CreteOrderResponse(
    @Serializable(with = UUIDSerializer::class)
    val orderId: UUID,
    @Serializable(with = UUIDSerializer::class)
    val requestId: UUID,
    @Serializable(with = UUIDSerializer::class)
    val customerId: UUID,
    val items: List<OrderItemDto>,
    val totalAmount: Double,
    val currency: CurrenyType,
    var status: OrderStatus,
    @Serializable(with = InstantSerializer::class)
    var createdAt: Instant,
    @Serializable(with = InstantSerializer::class)
    var updatedAt: Instant?,
    var payment: OrderPaymentDto
)