package org.example.infra.database

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.example.application.port.PaymentRepository
import org.example.domain.Payment
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

class PostgresPaymentRepository : PaymentRepository {
    override suspend fun save(payment: Payment) {
        withContext(Dispatchers.IO) {
            transaction {
                PaymentModel.insert {
                    it[paymentId] = payment.paymentId
                    it[orderId] = payment.orderId
                    it[status] = payment.status.name
                    it[amount] = payment.amount
                    it[currency] = payment.currency.name
                    it[reason] = payment.reason
                    it[processedAt] = payment.processedAt
                }
            }
        }
    }
}