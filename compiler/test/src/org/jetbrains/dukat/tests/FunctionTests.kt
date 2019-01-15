package org.jetbrains.dukat.tests

import kotlin.test.Test


class FunctionTests : StandardTests() {
    @Test
    fun testFunctions() {
        assertContentEquals("topLevelMembers/functions/functions")
    }

    @Test
    fun testFunctionsWithDefaultArguments() {
        assertContentEquals("topLevelMembers/functions/functionsWithDefaultArguments")
    }

    @Test
    fun functionsWithOptionalFunctionType() {
        assertContentEquals("topLevelMembers/functions/functionsWithOptionalFunctionType")
    }

    @Test
    fun functionsWithOptionalParameter() {
        assertContentEquals("topLevelMembers/functions/functionsWithOptionalParameter")
    }

    @Test
    fun functionsWithVararg() {
        assertContentEquals("topLevelMembers/functions/functionsWithVararg")
    }

}