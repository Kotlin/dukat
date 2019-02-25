package org.jetbrains.dukat.compiler.tests

import org.junit.Test

class FunctionTests : StandardTests() {
    @Test
    fun functions() {
        assertContentEquals("topLevelMembers/functions/functions")
    }

    @Test
    fun functionsWithDefaultArguments() {
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