package org.jetbrains.dukat.compiler.tests

import org.jetbrains.kotlin.cli.common.messages.CompilerMessageLocation
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.cli.common.messages.MessageCollector

class CompileMessageCollector : MessageCollector {
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

            System.err.println("[failure] ${severity} ${message} ${location}")
        } else {
            println("[warning] ${severity} ${message} ${location}")
        }
    }
}