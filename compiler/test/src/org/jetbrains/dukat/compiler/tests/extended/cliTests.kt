package org.jetbrains.dukat.compiler.tests.extended

import org.jetbrains.dukat.compiler.tests.httpService.CliHttpClient
import org.jetbrains.dukat.compiler.tests.httpService.CliHttpService
import org.junit.jupiter.api.extension.AfterAllCallback
import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.ExtensionContext

private var CLI_PROCESS: Process? = null
private val PORT = "8090"

class CliTestsStarted : BeforeAllCallback {
    override fun beforeAll(context: ExtensionContext?) {
        CLI_PROCESS = CliHttpService().startService(PORT)
        CliHttpClient(PORT).waitForServer()
        println("cli http process creation: ${CLI_PROCESS?.isAlive}")
    }
}

class CliTestsEnded : AfterAllCallback {
    override fun afterAll(context: ExtensionContext?) {
        CLI_PROCESS?.destroy()
        println("shutting down cli http process")
    }
}
