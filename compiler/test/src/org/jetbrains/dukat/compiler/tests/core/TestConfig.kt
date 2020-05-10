package org.jetbrains.dukat.compiler.tests.core

object TestConfig {
    val DEFINITELY_TYPED_DIR = System.getProperty("dukat.test.resources.definitelyTyped")
    val TOPN_DIR = System.getProperty("dukat.test.resources.topN")
    const val CLI_TEST_SERVER_PORT = "8090"
}