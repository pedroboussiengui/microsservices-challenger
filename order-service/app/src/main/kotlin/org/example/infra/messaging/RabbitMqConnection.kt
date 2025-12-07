package org.example.infra.messaging

import com.rabbitmq.client.Connection
import com.rabbitmq.client.ConnectionFactory

object RabbitMqConnection {
    fun init(host: String, port: Int, username: String, password: String): Connection {
        val factory = ConnectionFactory().apply {
            this.host = host
            this.port = port
            this.username = username
            this.password = password
        }
        return factory.newConnection()
    }
}
//    val factory = ConnectionFactory().apply {
//        host = "localhost"
//        port = 5672
//        username = "guest"
//        password = "guest"
//    }
