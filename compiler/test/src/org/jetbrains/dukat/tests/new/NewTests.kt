package org.jetbrains.dukat.tests.new

import org.jetbrains.dukat.tests.StandardTests
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