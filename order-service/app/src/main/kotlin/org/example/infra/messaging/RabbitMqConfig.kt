package org.example.infra.messaging

import com.rabbitmq.client.Connection

class RabbitMqConfig(
    private val connection: Connection
) {
    fun setup() {
        val channel = connection.createChannel()
        try {
            channel.exchangeDeclare("order", "topic", true)
            channel.exchangeDeclare("payment", "topic", true)

            channel.queueDeclare("order.created.queue", true, false, false, null)
            channel.queueBind("order.created.queue", "order", "order.created")

            channel.queueDeclare("payment.approved.queue", true, false, false, null)
            channel.queueBind("payment.approved.queue", "payment", "payment.approved")

            channel.queueDeclare("payment.rejected.queue", true, false, false, null)
            channel.queueBind("payment.rejected.queue", "payment", "payment.rejected")
        } finally {
            channel.close()
        }
    }
}