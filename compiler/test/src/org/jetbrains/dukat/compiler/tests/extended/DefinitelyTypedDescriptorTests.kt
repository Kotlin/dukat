package org.jetbrains.dukat.compiler.tests.extended

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.condition.EnabledIfSystemProperty
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

class DefinitelyTypedDescriptorTests : CompilationTests() {

    @DisplayName("DefinitelyTyped descriptor test set")
    @ParameterizedTest(name = "{0}")
    @MethodSource("descriptorCompilationSet")
    @EnabledIfSystemProperty(named = "dukat.test.extended", matches = "true")
    override fun runTests(descriptor: String, sourcePath: String, tsConfig: String) {
        assertDescriptorEquals(descriptor, sourcePath, if (tsConfig.isEmpty()) null else tsConfig)
    }

    companion object {
        @JvmStatic
        fun descriptorCompilationSet(): Array<Array<String>> = DefinitelyTypedCompilationTests.extendedSet()
    }

}