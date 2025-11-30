package org.example.infra.database

import org.example.domain.Item
import org.example.domain.Order
import org.example.domain.Payment
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import java.util.UUID

interface OrderRepository  {
    fun save(order: Order)
    fun findById(id: UUID): Order?
}

class PostgresOrderRepository : OrderRepository {
    override fun save(order: Order) {
        transaction {
            OrderModel.insert {
                it[id] = order.id
                it[requestId] = order.requestId
                it[customerId] = order.customerId
                it[totalAmount] = order.totalAmount
                it[currency] = order.currency
                it[status] = order.status
                it[createdAt] = order.createdAt
                it[updatedAt] = order.updatedAt
                it[paymentId] = order.payment.payment
                it[paymentStatus] = order.payment.paymentStatus
                it[processedAt] = order.payment.processedAt
            }
            order.items.forEach { item ->
                OrderItemModel.insert {
                    it[id] = UUID.randomUUID()
                    it[orderId] = order.id
                    it[productId] = item.productId
                    it[quantity] = item.quantity
                    it[unitPrice] = item.unitPrice
                }
            }
        }
    }

    override fun findById(id: UUID): Order? {
        return transaction {
            val orderRow = OrderModel.selectAll().where { OrderModel.id eq id }.singleOrNull()
                ?: return@transaction null
            val orderItemsRows = OrderItemModel.selectAll().where { OrderItemModel.orderId eq id }

            val items = orderItemsRows.map {
                Item(
                    productId = it[OrderItemModel.productId],
                    quantity = it[OrderItemModel.quantity],
                    unitPrice = it[OrderItemModel.unitPrice]
                )
            }

            Order(
                id = orderRow[OrderModel.id],
                requestId = orderRow[OrderModel.requestId],
                customerId = orderRow[OrderModel.customerId],
                items = items,
                totalAmount = orderRow[OrderModel.totalAmount],
                currency = orderRow[OrderModel.currency],
                status = orderRow[OrderModel.status],
                createdAt = orderRow[OrderModel.createdAt],
                updatedAt = orderRow[OrderModel.updatedAt],
                payment = Payment(
                    payment = orderRow[OrderModel.paymentId],
                    paymentStatus = orderRow[OrderModel.paymentStatus],
                    processedAt = orderRow[OrderModel.processedAt]
                )
            )
        }
    }
}