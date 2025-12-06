package org.example.infra.opentelemetry

import io.opentelemetry.api.logs.Logger
import io.opentelemetry.api.logs.Severity
import io.opentelemetry.api.trace.StatusCode
import io.opentelemetry.api.trace.Tracer
import org.example.application.port.ObservabilityPort

class OpenTelemetryAdapter(
    private val tracer: Tracer,
    private val logger: Logger
): ObservabilityPort {

    override suspend fun <T> inSpan(name: String, block: suspend () -> T): T {
        val span = tracer.spanBuilder(name).startSpan()
        span.makeCurrent().use {
            logger.logRecordBuilder()
                .setBody("Span started: $name")
                .setSeverity(Severity.INFO)
                .emit()
            return try {
                block()
            } catch (e: Exception) {
                span.recordException(e)
                span.setStatus(StatusCode.ERROR)
                logger.logRecordBuilder()
                    .setBody("Error in span '$name': ${e.message}")
                    .setSeverity(Severity.ERROR)
                    .emit()
                throw e
            } finally {
                span.end()
                logger.logRecordBuilder()
                    .setBody("Span ended: $name")
                    .setSeverity(Severity.INFO)
                    .emit()
            }
        }
    }
}