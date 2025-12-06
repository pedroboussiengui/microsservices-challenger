package org.example.application.port

import org.example.domain.Payment

interface PaymentRepository {
    suspend fun save(payment: Payment)
}