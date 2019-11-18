package org.jetbrains.dukat.compiler.tests.core

object TestConfig {
    val DEFAULT_LIB_PATH = "../ts/build/package/node_modules/typescript/lib/lib.es6.d.ts"
    val NODE_PATH = "node"
    val CONVERTER_SOURCE_PATH = "../ts/build/bundle/converter.js"
    val DEFINITELY_TYPED_DIR = System.getProperty("dukat.test.resources.definitelyTyped")
    val TOPN_DIR = System.getProperty("dukat.test.resources.topN")
    val COMPILATION_TIMEOUT_MILLIS = 240000L
}