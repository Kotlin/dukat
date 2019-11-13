package org.jetbrains.dukat.compiler.tests.extended

import org.jetbrains.dukat.compiler.tests.core.TestConfig.DEFINITELY_TYPED_DIR
import org.jetbrains.dukat.compiler.tests.core.TestConfig.TOPN_DIR
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
                    "@types/async",
                    "@types/bluebird",
                    "@types/body-parser",
                    "@types/commander",
                    "@types/express",
                    "@types/fs-extra",
                    "@types/lodash",
                    "@types/moment",
                    "@types/prop-types",
                    "@types/react",
                    "@types/react-dom",
                    "@types/request",
                    "@types/underscore",
                    "@types/webpack",
                    "@types/yargs",
                    "axios",
                    "chalk",
                    "commander",
                    "rxjs",
                    "tslib",
                    "vue/types"
            ).map { descriptor ->
                arrayOf(
                        descriptor,
                        "$TOPN_DIR/node_modules/$descriptor/index.d.ts"
                )
            }.toTypedArray()

        }

    }
}