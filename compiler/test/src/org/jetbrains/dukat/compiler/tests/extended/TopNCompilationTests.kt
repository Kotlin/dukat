package org.jetbrains.dukat.compiler.tests.extended

import org.jetbrains.dukat.compiler.tests.core.TestConfig.TOPN_DIR
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.condition.EnabledIfSystemProperty
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.io.File

class TopNCompilationTests : CompilationTests() {

    @DisplayName("core test set compile")
    @ParameterizedTest(name = "{0}")
    @MethodSource("extendedSet")
    @EnabledIfSystemProperty(named = "dukat.test.extended.topn", matches = "true")
    override fun runTests(
            descriptor: String,
            sourcePath: String,
            tsConfig: String
    ) {
        assertContentCompiles(descriptor, sourcePath)
    }

    companion object {

        @JvmStatic
        fun extendedSet(): Array<Array<String>> {
            val minimalCore = listOf(
                    "@types/async",
                    "@types/lodash",
                    "@types/prop-types",
                    "@types/yargs",
                    "axios",
                    "chalk",
                    "moment/moment.d.ts",
                    "tslib/tslib.d.ts"
            )

            val allTests = minimalCore + listOf(
                    "@types/bluebird",
                    "@types/body-parser",
                    "@types/express",
                    "@types/fs-extra",
                    "@types/jquery",
                    "@types/node",
                    "@types/react",
                    "@types/react-dom",
                    "@types/request",
                    "@types/underscore",
                    "@types/webpack",
                    "commander/typings",
                    "rxjs",
                    "vue/types"
            )

            return (if (System.getProperty("dukat.test.extended.topn") == "true") {
                allTests
            } else {
                minimalCore
            }).sorted().map { descriptor ->
                val name = if (descriptor.endsWith("d.ts")) {
                    descriptor
                } else {
                    "$descriptor/index.d.ts"
                }
                arrayOf(
                        descriptor,
                        File("$TOPN_DIR/node_modules/$name").normalize().absolutePath,
                        ""
                )
            }.toTypedArray()
        }

    }
}