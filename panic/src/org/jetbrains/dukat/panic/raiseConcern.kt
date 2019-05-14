package org.jetbrains.dukat.panic

import org.jetbrains.dukat.logger.Logging

private val logger = Logging.logger("panic")

fun <T> raiseConcern(message: String, fallback: () -> T): T {
    val mode = getPanicMode()
    logger.debug(message)
    return when (mode) {
        PanicMode.ALWAYS_FAIL -> throw Exception(message)
        PanicMode.NEVER_FAIL -> fallback.invoke()
    }
}