package org.example.application.port

import kotlinx.serialization.KSerializer

interface MessagePublisher {
    suspend fun <T : Any> publish(
        exchange: String,
        routingKey: String,
        payload: T,
        serializer: KSerializer<T>,
        headers: Map<String, String> = emptyMap()
    )
}