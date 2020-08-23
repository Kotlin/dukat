package org.jetbrains.dukat.compiler.tests

import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSourceLocation
import org.jetbrains.kotlin.cli.common.messages.MessageCollector

internal fun String.toFileUriScheme(): String {
    return "file:///${this.replace(System.getProperty("file.separator"), "/")}"
}

class CompileMessageCollector(private val onError: (message: String, severity: CompilerMessageSeverity, location: CompilerMessageSourceLocation?) -> Unit) : MessageCollector {
    private var myHasErrors: Boolean = false

    override fun clear() {
        myHasErrors = false
    }

    override fun hasErrors(): Boolean {
        return false
    }

    override fun report(severity: CompilerMessageSeverity, message: String, location: CompilerMessageSourceLocation?) {
        if (severity.isError) {
            myHasErrors = true

            onError(message, severity, location)
            val source = "${location?.path}:${location?.line}:${location?.column}"
            System.err.println("[failure] ${severity} ${message.take(5000)} ${source.toFileUriScheme()}")
        } else {
            println("[warning] ${severity} ${message} ${location}")
        }
    }
}