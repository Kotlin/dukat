package org.jetbrains.dukat.compiler.tests.core

import org.jetbrains.dukat.compiler.translator.IdlInputTranslator
import org.jetbrains.dukat.idlReferenceResolver.DirectoryReferencesResolver
import org.jetbrains.dukat.translator.InputTranslator
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.io.File

class Idl2ktDynamicAsTypeTests : Idl2KtTestBase() {

    @DisplayName("idl2kt with dynamic as type")
    @Test
    fun idl2ktStaticGettersTest() {
        val tsPath = File("./test/data/idl2kt/simpleTest.webidl").absolutePath
        val ktPath = File("./test/data/idl2kt/simpleTest.d.dynamicAsType.kt").absolutePath
        assertContentEquals("idl2ktDynamicAsTypeTest", tsPath, ktPath)
    }

    override fun getTranslator(): InputTranslator<String> =
        IdlInputTranslator(DirectoryReferencesResolver(), dynamicAsType = true)
}