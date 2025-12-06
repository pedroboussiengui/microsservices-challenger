package org.example.application.port

interface ObservabilityPort {
    suspend fun <T> inSpan(name: String, block: suspend () -> T): T
}