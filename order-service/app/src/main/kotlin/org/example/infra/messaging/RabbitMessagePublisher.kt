package org.example.infra.messaging

import com.rabbitmq.client.AMQP
import com.rabbitmq.client.Connection
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import org.example.application.port.MessagePublisher
import java.nio.charset.StandardCharsets

class RabbitMessagePublisher(
    private val connection: Connection,
    private val json: Json = Json
) : MessagePublisher {
    override suspend fun <T : Any> publish(
        exchange: String,
        routingKey: String,
        payload: T,
        serializer: KSerializer<T>,
        headers: Map<String, String>
    ) {
        withContext(Dispatchers.IO) {
            val channel = connection.createChannel()
            channel.exchangeDeclare(exchange, "topic", true)
            channel.queueDeclare("order.created.queue", true, false, false, null)
            channel.queueBind("order.created.queue", "order", "order.created")

            try {
                val body = json.encodeToString(serializer, payload)
                    .toByteArray(StandardCharsets.UTF_8)

                val props = AMQP.BasicProperties.Builder()
                    .contentType("application/json")
                    .headers(headers)
                    .deliveryMode(2) //persistent
                    .build()

                channel.basicPublish(
                    exchange,
                    routingKey,
                    props,
                    body
                )
            } finally {
                channel.close(
                )
            }
        }
    }
}