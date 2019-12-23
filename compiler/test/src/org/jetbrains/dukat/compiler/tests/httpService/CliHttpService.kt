package org.jetbrains.dukat.compiler.tests.httpService

import org.jetbrains.dukat.compiler.tests.NodeResolver

class CliHttpService() {
    private val serverPath: String = "./test/src/org/jetbrains/dukat/compiler/tests/server.js"
    private val nodeResolver = NodeResolver("../node-package/build/env.json")
    private val maxOldSpaceSize = 8192

    fun startService(port: String): Process {
        return ProcessBuilder().inheritIO().command(nodeResolver.nodePath, "--max-old-space-size=${maxOldSpaceSize}", serverPath, port).start()
    }
}