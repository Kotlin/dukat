package org.jetbrains.dukat.panic

import org.jetbrains.dukat.logger.Logging

private val logger = Logging.logger("panic")

fun <T> raiseConcern(message: String, fallback: () -> T): T {
    logger.debug(message)
    return fallback.invoke()
}