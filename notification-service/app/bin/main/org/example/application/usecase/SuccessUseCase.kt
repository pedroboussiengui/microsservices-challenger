package org.example.application.usecase

import org.example.application.port.ObservabilityPort

class SuccessUseCase(
    private val observabilityPort: ObservabilityPort
) {
    suspend fun execute(): String =
        observabilityPort.inSpan("SuccessUseCase") {
            "Success"
        }
}