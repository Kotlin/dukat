package org.jetbrains.dukat.panic

import org.jetbrains.dukat.logger.Logging

internal data class PanicOptions(var mode: PanicMode)

private val PANIC_OPTIONS = PanicOptions(PanicMode.NEVER_FAIL)

fun setPanicMode(mode: PanicMode) {
    PANIC_OPTIONS.mode = mode
}

fun getPanicMode(): PanicMode {
    return PANIC_OPTIONS.mode
}

private val logger = Logging.logger("panicOptions")

fun resolvePanicMode() {
    if (System.getProperty("dukat.test.failure.always") == "true") {
        setPanicMode(PanicMode.ALWAYS_FAIL)
    }

    logger.debug("panic mode resolved to ${PANIC_OPTIONS.mode}")
}


