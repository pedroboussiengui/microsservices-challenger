package org.example

import org.example.domain.CurrenyType
import org.example.domain.Item
import org.example.domain.Order
import org.example.domain.OrderStatus
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.util.*
import kotlin.time.ExperimentalTime

class OrderTest {

    @OptIn(ExperimentalTime::class)
    @Test fun `should create an order successfully`() {
        // Given
        val requestId = UUID.randomUUID()
        val customerId = UUID.randomUUID()
        val items = listOf(
            Item(UUID.randomUUID(), 2, 10.0),
            Item(UUID.randomUUID(), 1, 2.0,)
        )
        val currency = CurrenyType.BRL

        // When
        val order = Order.create(requestId, customerId, items, currency)

        // Then
        assertNotNull(order.id)
        assertEquals(requestId, order.requestId)
        assertEquals(customerId, order.customerId)
        assertEquals(items, order.items)
        assertEquals(22.0, order.totalAmount)
        assertEquals(currency, order.currency)
        assertEquals(OrderStatus.PENDING, order.status)
        assertNotNull(order.createdAt)
        assertNull(order.updatedAt)
        assertNotNull(order.payment)
    }

    @OptIn(ExperimentalTime::class)
    @Test fun `should confirm an order successfully`() {
        // Given
        val requestId = UUID.randomUUID()
        val customerId = UUID.randomUUID()
        val items = listOf(
            Item(UUID.randomUUID(), 2, 10.0),
            Item(UUID.randomUUID(), 1, 2.0,)
        )
        val currency = CurrenyType.BRL
        val order = Order.create(requestId, customerId, items, currency)

        // When
        order.confirmOrder()

        // Then
        assertEquals(OrderStatus.CONFIRMED, order.status)
        assertNotNull(order.updatedAt)
    }
}