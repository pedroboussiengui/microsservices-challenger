package org.example.application.usecase

import org.example.application.port.ObservabilityPort

class ErrorUseCase(
    private val observabilityPort: ObservabilityPort
) {
    suspend fun execute(): String =
        observabilityPort.inSpan("ErrorUseCase") {
            throw IllegalStateException("Falha simulada no usecase!")
        }
}