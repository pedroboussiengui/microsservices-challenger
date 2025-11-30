package org.example.application.usecase

import io.ktor.server.plugins.NotFoundException
import org.example.infra.database.OrderRepository
import java.util.UUID

class RetrieveOrderById(
    val orderRepository: OrderRepository
) {
    fun execute(input: UUID): CreteOrderResponse {
        val order = orderRepository.findById(input)
            ?: throw NotFoundException("Order with id $input not found")

        val itemsResponse = order.items.map {
            OrderItemDto(
                productId = it.productId,
                quantity = it.quantity,
                unitPrice = it.unitPrice
            )
        }
        val paymentResponse = if (order.payment != null) {
            OrderPaymentDto(
                payment = order.payment!!.payment,
                paymentStatus = order.payment!!.paymentStatus,
                processedAt = order.payment!!.processedAt
            )
        } else null
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