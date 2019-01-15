package org.jetbrains.dukat.tests

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