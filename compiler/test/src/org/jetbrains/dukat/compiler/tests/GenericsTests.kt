package org.jetbrains.dukat.compiler.tests

import org.junit.Test

class GenericsTests : StandardTests() {
    @Test
    fun generics() {
        assertContentEquals("topLevelMembers/generics/generics")
    }

    @Test
    fun genericsWithConstraint() {
        assertContentEquals("topLevelMembers/generics/genericsWithConstraint")
    }
}