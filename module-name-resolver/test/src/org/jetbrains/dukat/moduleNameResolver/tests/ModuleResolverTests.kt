package org.jetbrains.dukat.moduleNameResolver.tests

import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.moduleNameResolver.CommonJsNameResolver
import org.jetbrains.dukat.moduleNameResolver.ConstNameResolver
import org.jetbrains.dukat.moduleNameResolver.ModuleNameResolver
import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.assertEquals

class ModuleResolverTests {

    @Test
    fun resolveModuleA() {
        resolve("moduleA/lib/moduleA.d.ts", IdentifierEntity("moduleA"))
    }

    @Test
    fun resolveModuleB() {
        resolve("moduleB/lib/moduleB.d.ts", IdentifierEntity("moduleB"))
    }

    @Test
    fun resolveModuleC() {
        resolve("moduleC/lib/moduleC.d.ts", null)
    }

    @Test
    fun resolveModuleD() {
        resolve("moduleD/lib/deeply/nested/moduleD.d.ts", IdentifierEntity("moduleD"))
    }

    @Test
    fun resolveModuleE() {
        resolve("moduleE/lib/deeply/nested/moduleE.d.ts", null)
    }

    @Test
    fun resolveModuleWithName() {
        resolve("whatever/path/we/pass.txt", IdentifierEntity("mylib"), ConstNameResolver(IdentifierEntity("mylib")))
    }

    private fun resolve(path: String, expected: NameEntity?, resolver: ModuleNameResolver = CommonJsNameResolver()) {
        val prefix = "./test/data/node_modules"
        val fullPath = File(prefix, path).absolutePath
        assertEquals(resolver.resolveName(fullPath), expected)
    }
}