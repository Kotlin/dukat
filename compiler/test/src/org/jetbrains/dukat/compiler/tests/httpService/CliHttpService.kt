package org.jetbrains.dukat.compiler.tests.httpService

import kotlinx.serialization.UnstableDefault
import org.jetbrains.dukat.compiler.tests.NodeResolver
import org.jetbrains.dukat.compiler.tests.core.TestConfig

class CliHttpService @OptIn(UnstableDefault::class) constructor(
        private val serverPath: String = "./test/src/org/jetbrains/dukat/compiler/tests/server.js",
        private val nodeResolver: NodeResolver = NodeResolver("../node-package/build/env.json"),
        private val inspectNodeProcess: Boolean = false
) {

    @OptIn(UnstableDefault::class)
    private val maxOldSpaceSize = 8192

    @OptIn(UnstableDefault::class)
    fun start(
            port: String,
            vararg sandboxDirs: String
    ): Process {
        val args = listOf(
                nodeResolver.nodePath,
                if (inspectNodeProcess) "--inspect" else null,
                "--max-old-space-size=${maxOldSpaceSize}",
                serverPath,
                port,
                *sandboxDirs
        ).filterNotNull().toTypedArray()
        return ProcessBuilder().inheritIO().command(*args).start()
    }

    fun start() = start(
            TestConfig.CLI_TEST_SERVER_PORT,
            *listOf(TestConfig.DEFINITELY_TYPED_DIR).filterNotNull().toTypedArray()
    )
}

@OptIn(UnstableDefault::class)
fun main() {
    CliHttpService(
            serverPath = "./compiler/test/src/org/jetbrains/dukat/compiler/tests/server.js",
            nodeResolver = NodeResolver("./node-package/build/env.json"),
            inspectNodeProcess = true
    ).start()
}