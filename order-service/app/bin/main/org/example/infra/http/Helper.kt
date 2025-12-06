package org.example.infra.http

import io.ktor.http.*
import org.valiktor.ConstraintViolationException
import org.valiktor.i18n.mapToMessage
import java.util.*

fun Parameters.uuid(input: String): UUID? =
    this[input]?.let {
        try {
            UUID.fromString(it)
        } catch (_: IllegalArgumentException) {
            null
        }
    }


fun validationErrors(e: ConstraintViolationException): List<String> =
    e.constraintViolations
        .mapToMessage(baseName = "messages", locale = Locale.ENGLISH)
        .map { "${it.property}: ${it.message}" }
