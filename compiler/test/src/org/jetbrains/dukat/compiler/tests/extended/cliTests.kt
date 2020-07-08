package org.jetbrains.dukat.compiler.tests.extended

import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.jetbrains.dukat.compiler.tests.core.TestConfig
import org.jetbrains.dukat.compiler.tests.httpService.CliHttpClient
import org.jetbrains.dukat.compiler.tests.httpService.CliHttpService
import org.junit.jupiter.api.extension.AfterAllCallback
import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.ExtensionContext

class CliTestsStarted : BeforeAllCallback {
    override fun beforeAll(context: ExtensionContext?) {
        CliHttpService(emitDiagnostics = System.getProperty("dukat.test.emitTsDiagnostics") == "true").start()
        runBlocking {
            async {
                CliHttpClient(TestConfig.CLI_TEST_SERVER_PORT).waitForServer()
            }.await()
        }
    }
}

class CliTestsEnded : AfterAllCallback {
    override fun afterAll(context: ExtensionContext?) {
        println("shutting down cli http process")
        CliHttpClient(TestConfig.CLI_TEST_SERVER_PORT).shutdown()
    }
}
