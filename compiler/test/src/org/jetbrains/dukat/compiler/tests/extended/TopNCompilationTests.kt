package org.jetbrains.dukat.compiler.tests.extended

import org.jetbrains.dukat.compiler.tests.core.TestConfig.DEFINITELY_TYPED_DIR
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.condition.EnabledIfSystemProperty
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

class TopNCompilationTests : CompilationTests() {

    @DisplayName("core test set compile")
    @ParameterizedTest(name = "{0}")
    @MethodSource("extendedSet")
    @EnabledIfSystemProperty(named = "dukat.test.extended", matches = "true")
    override fun runTests(
            descriptor: String,
            sourcePath: String
    ) {
        assertContentCompiles(descriptor, sourcePath)
    }

    companion object {

        @JvmStatic
        fun extendedSet(): Array<Array<String>> {

            return listOf(
                    "lodash",
                    "request",
                    "react",
                    "express",
                    "prop-types",
                    "react-dom",
                    "async",
                    "fs-extra",
                    "bluebird",
                    "underscore",
                    "webpack",
                    "yargs",
                    "body-parser"
            ).map { descriptor ->
                arrayOf(
                        descriptor,
                        "$DEFINITELY_TYPED_DIR/$descriptor/index.d.ts"
                )
            }.toTypedArray()

        }

    }
}