package org.example.infra.database

import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.javatime.timestamp

object PaymentModel : Table("payment_service.payments") {
    val paymentId = uuid("payment_id")
    val orderId = uuid("order_id")
    val status = varchar("status", 50)
    val amount = double("amount")
    val currency = varchar("currency", 10)
    val reason = varchar("reason", 255).nullable()
    val processedAt = timestamp("processed_at")

    override val primaryKey = PrimaryKey(paymentId)
}