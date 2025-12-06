package org.example.infra.di

import io.ktor.server.application.Application
import io.ktor.util.AttributeKey
import io.opentelemetry.api.OpenTelemetry
import io.opentelemetry.api.logs.GlobalLoggerProvider
import org.example.application.usecase.ErrorUseCase
import org.example.application.usecase.SuccessUseCase
import org.example.infra.opentelemetry.OpenTelemetryAdapter
import org.example.infra.opentelemetry.OtelConfig

private val otelConfigLey = AttributeKey<OpenTelemetry>("otelConfig")
private val openTelemetryAdapterKey = AttributeKey<OpenTelemetryAdapter>("openTelemetryAdapter")
private val successUseCaseKey = AttributeKey<SuccessUseCase>("successUseCase")
private val errorUseCaseKey = AttributeKey<ErrorUseCase>("errorUseCase")

fun Application.configureDI() {
    val otel = OtelConfig.init()
    attributes.put(otelConfigLey, otel)

    val obs = OpenTelemetryAdapter(
        tracer = otel.getTracer("ktor-app"),
        logger = GlobalLoggerProvider.get().loggerBuilder("demo-builder").build()
    )
    attributes.put(openTelemetryAdapterKey, obs)

    val error = ErrorUseCase(obs)
    attributes.put(errorUseCaseKey, error)

    val success = SuccessUseCase(obs)
    attributes.put(successUseCaseKey, success)
}

val Application.successUseCase: SuccessUseCase
    get() = attributes[successUseCaseKey]

val Application.errorUseCase: ErrorUseCase
    get() = attributes[errorUseCaseKey]
