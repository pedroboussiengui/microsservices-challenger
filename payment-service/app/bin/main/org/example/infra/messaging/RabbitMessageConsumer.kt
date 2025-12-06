package org.example.infra.messaging

import com.rabbitmq.client.AMQP
import com.rabbitmq.client.Connection
import com.rabbitmq.client.DefaultConsumer
import com.rabbitmq.client.Envelope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import org.example.application.port.MessageConsumer

class RabbitMessageConsumer(
    private val connection: Connection
) : MessageConsumer {

    private val channel = connection.createChannel()
    private val json = Json {
        ignoreUnknownKeys = true
    }

    override suspend fun <T : Any> consume(
        queue: String,
        serializer: KSerializer<T>,
        handler: suspend (T, Map<String, Any?>) -> Unit
    ) {
        channel.basicQos(1) //processa 1 msg por vez

        val consumer = object : DefaultConsumer(channel) {
            override fun handleDelivery(
                consumerTag: String?,
                envelope: Envelope?,
                properties: AMQP.BasicProperties?,
                body: ByteArray?
            ) {
                try {
                    val message = json.decodeFromString(serializer, body!!.decodeToString())
                    val headers = properties?.headers ?: emptyMap()

                    // executa seu handler suspending
                    GlobalScope.launch {
                        handler(message, headers)
                        channel.basicAck(envelope!!.deliveryTag, false)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    channel.basicNack(envelope!!.deliveryTag, false, false)
                }
            }
        }

        println("📥 Waiting for messages from $queue ...")
        channel.basicConsume(queue, false, consumer)
    }
}