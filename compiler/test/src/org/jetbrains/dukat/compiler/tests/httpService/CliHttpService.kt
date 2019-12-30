package org.jetbrains.dukat.compiler.tests.httpService

import kotlinx.serialization.UnstableDefault
import org.jetbrains.dukat.compiler.tests.NodeResolver

class CliHttpService() {
    private val serverPath: String = "./test/src/org/jetbrains/dukat/compiler/tests/server.js"
    @UseExperimental(UnstableDefault::class)
    private val nodeResolver = NodeResolver("../node-package/build/env.json")
    private val maxOldSpaceSize = 8192

    @UseExperimental(UnstableDefault::class)
    fun startService(port: String): Process {
        return ProcessBuilder().inheritIO().command(nodeResolver.nodePath, "--max-old-space-size=${maxOldSpaceSize}", serverPath, port).start()
    }
}