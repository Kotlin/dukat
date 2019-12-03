package org.jetbrains.dukat.compiler.tests

import org.jetbrains.kotlin.cli.common.messages.CompilerMessageLocation
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.cli.common.messages.MessageCollector

class CompileMessageCollector(private val onError: (message: String, severity: CompilerMessageSeverity, location: CompilerMessageLocation?) -> Unit) : MessageCollector {
    private var myHasErrors: Boolean = false

    override fun clear() {
        myHasErrors = false
    }

    override fun hasErrors(): Boolean {
        return false
    }

    override fun report(severity: CompilerMessageSeverity, message: String, location: CompilerMessageLocation?) {
        if (severity.isError) {
            myHasErrors = true

            onError(message, severity, location)
            System.err.println("[failure] ${severity} ${message} file:///${location?.path}:${location?.line}:${location?.column}")
        } else {
            println("[warning] ${severity} ${message} ${location}")
        }
    }
}