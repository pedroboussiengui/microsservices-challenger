package org.example.infra.database

import org.example.domain.CurrenyType
import org.example.domain.OrderStatus
import org.example.domain.PaymentStatus
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.javatime.timestamp

object OrderModel : Table("order_service.orders") {
    val id = uuid("id")
    val requestId = uuid("request_id")
    val customerId = uuid("customer_id")
    val totalAmount = double("total_amount")
    val currency = enumerationByName("currency_type", 3, CurrenyType::class)
    val status = enumeration("status", OrderStatus::class)
    val createdAt = timestamp("created_at")
    val updatedAt = timestamp("updated_at").nullable()
    val paymentId = uuid("payment_id").nullable()
    val paymentStatus = enumeration("payment_status", PaymentStatus::class).nullable()
    val processedAt = timestamp("processed_at").nullable()

    override val primaryKey = PrimaryKey(id)
}

object OrderItemModel : Table("order_service.order_items") {
    val id = uuid("id")
    val orderId = reference("order_id", OrderModel.id)
    val productId = uuid("product_id")
    val quantity = integer("quantity")
    val unitPrice = double("unit_price")

    override val primaryKey = PrimaryKey(id)
}
