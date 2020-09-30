package org.jetbrains.dukat.compiler.tests.core

import java.io.File

object TestConfig {
    val DEFINITELY_TYPED_DIR = System.getProperty("dukat.test.resources.definitelyTyped")
    val TOPN_DIR = System.getProperty("dukat.test.resources.topN")
    const val CLI_TEST_SERVER_PORT = "8090"
    val STDLIB_JAR = File("./build/lib/kotlin-stdlib-js.jar").absolutePath
}

