package org.jetbrains.dukat.tests

import org.junit.Test

class VariablesTests : StandardTests() {
    @Test
    fun testVariables() {
        assertContentEquals("topLevelMembers/variables/variables")
    }

    @Test
    fun testVariableAsFunctionType() {
        assertContentEquals("topLevelMembers/variables/variableAsFunctionType")
    }

    @Test
    fun testVariablesAsArray() {
        assertContentEquals("topLevelMembers/variables/variablesAsArray")
    }

    @Test
    fun testVariablesWithoutDeclare() {
        assertContentEquals("topLevelMembers/variables/variablesWithoutDeclare")
    }
}