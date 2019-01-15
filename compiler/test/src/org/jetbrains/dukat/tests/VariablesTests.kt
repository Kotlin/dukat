package org.jetbrains.dukat.tests

import org.junit.Test

class VariablesTests : StandardTests() {
    @Test
    fun variables() {
        assertContentEquals("topLevelMembers/variables/variables")
    }

    @Test
    fun variableAsFunctionType() {
        assertContentEquals("topLevelMembers/variables/variableAsFunctionType")
    }

    @Test
    fun variablesAsArray() {
        assertContentEquals("topLevelMembers/variables/variablesAsArray")
    }

    @Test
    fun variablesWithoutDeclare() {
        assertContentEquals("topLevelMembers/variables/variablesWithoutDeclare")
    }
}