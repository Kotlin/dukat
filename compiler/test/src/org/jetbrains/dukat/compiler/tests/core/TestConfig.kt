package org.jetbrains.dukat.compiler.tests.core

object TestConfig {
    val DEFAULT_LIB_PATH = "../ts/build/package/node_modules/typescript/lib/lib.d.ts"
    val NODE_PATH = "node"
    val CLI_SOURCE_PATH = "../node-package/build/distrib/bin/dukat-cli.js"
    val CONVERTER_SOURCE_PATH = "../ts/build/bundle/converter.js"
    val DEFINITELY_TYPED_DIR = System.getProperty("dukat.test.resources.definitelyTyped")
}