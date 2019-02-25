package org.jetbrains.dukat.compiler.tests.new

import org.jetbrains.dukat.compiler.tests.StandardTests
import org.junit.Test

class NewTests : StandardTests() {

    @Test
    fun inClass() {
        assertContentEquals("new/inTypeLiteral")
    }

    @Test
    fun simple() {
        assertContentEquals("new/simple")
    }


}