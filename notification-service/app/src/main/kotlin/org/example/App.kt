package org.example

import io.opentelemetry.api.common.AttributeKey
import io.opentelemetry.api.common.Attributes
import io.opentelemetry.api.logs.GlobalLoggerProvider
import io.opentelemetry.api.logs.Severity
import io.opentelemetry.exporter.otlp.logs.OtlpGrpcLogRecordExporter
import io.opentelemetry.exporter.otlp.trace.OtlpGrpcSpanExporter
import io.opentelemetry.sdk.OpenTelemetrySdk
import io.opentelemetry.sdk.logs.SdkLoggerProvider
import io.opentelemetry.sdk.logs.export.BatchLogRecordProcessor
import io.opentelemetry.sdk.resources.Resource
import io.opentelemetry.sdk.trace.SdkTracerProvider
import io.opentelemetry.sdk.trace.export.BatchSpanProcessor
import java.util.concurrent.TimeUnit

fun main() {
    val resource = Resource.getDefault().merge(
        Resource.create(
            Attributes.of(
                AttributeKey.stringKey("service.name"), "my-service",
                AttributeKey.stringKey("service.version"), "1.0.0"
            )
        )
    )

    // traces
    val spanExporter = OtlpGrpcSpanExporter.builder()
        .setEndpoint("http://localhost:4317")
        .build()

    val tracerProvider = SdkTracerProvider.builder()
        .setResource(resource)
        .addSpanProcessor(BatchSpanProcessor.builder(spanExporter).build())
        .build()

    // logs
    val logExporter = OtlpGrpcLogRecordExporter.builder()
        .setEndpoint("http://localhost:4317")
        .build()

    val loggerProvider = SdkLoggerProvider.builder()
        .setResource(resource)
        .addLogRecordProcessor(BatchLogRecordProcessor.builder(logExporter).build())
        .build()

    val openTelemetry = OpenTelemetrySdk.builder()
        .setTracerProvider(tracerProvider)
        .setLoggerProvider(loggerProvider)
        .buildAndRegisterGlobal()

    GlobalLoggerProvider.set(loggerProvider)

    val tracer = openTelemetry.getTracer("demo")
    val span = tracer.spanBuilder("test-span").startSpan()
    span.end()

    val logger = GlobalLoggerProvider.get().loggerBuilder("demo-builder").build()
    logger.logRecordBuilder()
        .setBody("Log de teste enviado para o Loki!")
        .setSeverity(Severity.INFO)
        .emit()

    tracerProvider.forceFlush().join(5, TimeUnit.SECONDS)
    tracerProvider.shutdown().join(5, TimeUnit.SECONDS)

    loggerProvider.forceFlush().join(5, TimeUnit.SECONDS)

    println("Span + Log enviado para o OTel Collector.")
}