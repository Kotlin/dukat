package org.jetbrains.dukat.compiler.tests.extended

import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.jetbrains.dukat.compiler.tests.core.TestConfig
import org.jetbrains.dukat.compiler.tests.httpService.CliHttpClient
import org.jetbrains.dukat.compiler.tests.httpService.CliHttpService
import org.junit.jupiter.api.extension.AfterAllCallback
import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.ExtensionContext

private var CLI_PROCESS: Process? = null

class CliTestsStarted : BeforeAllCallback {
    override fun beforeAll(context: ExtensionContext?) {
        CLI_PROCESS = CliHttpService().start()

        runBlocking {
            async {
                CliHttpClient(TestConfig.CLI_TEST_SERVER_PORT).waitForServer()
            }.await()
        }

        println("cli http process creation: ${CLI_PROCESS?.isAlive}")
    }
}

class CliTestsEnded : AfterAllCallback {
    override fun afterAll(context: ExtensionContext?) {
        CLI_PROCESS?.destroy()
        println("shutting down cli http process")
    }
}
