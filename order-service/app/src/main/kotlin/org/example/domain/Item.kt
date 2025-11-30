package org.example.domain

import java.util.UUID

class Item(
    val productId: UUID,
    val quantity: Int,
    val unitPrice: Double
) {
}