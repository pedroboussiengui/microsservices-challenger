package org.example.application.port

import kotlinx.serialization.KSerializer

interface MessageConsumer {
    suspend fun <T: Any> consume(
        queue: String,
        serializer: KSerializer<T>,
        handler: suspend (T, Map<String, Any?>) -> Unit
    )
}