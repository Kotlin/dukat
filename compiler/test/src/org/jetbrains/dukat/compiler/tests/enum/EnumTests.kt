package org.jetbrains.dukat.compiler.tests.enum

import org.jetbrains.dukat.compiler.tests.StandardTests
import org.junit.Test

class EnumTests : StandardTests() {

    @Test
    fun simple() {
        assertContentEquals("enum/simple")
    }

    @Test
    fun withValues() {
        assertContentEquals("enum/withValues")
    }

}