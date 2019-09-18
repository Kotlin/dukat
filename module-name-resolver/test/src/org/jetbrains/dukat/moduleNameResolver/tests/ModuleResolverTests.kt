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
        resolve("moduleA/lib/moduleA.d.ts", "moduleA")
    }

    @Test
    fun resolveModuleB() {
        resolve("moduleB/lib/moduleB.d.ts", "moduleB")
    }

    @Test
    fun resolveModuleC() {
        resolve("moduleC/lib/moduleC.d.ts", "moduleC")
    }

    @Test
    fun resolveModuleD() {
        resolve("moduleD/lib/deeply/nested/moduleD.d.ts", "moduleD")
    }

    @Test
    fun resolveModuleE() {
        resolve("moduleE/lib/deeply/nested/moduleE.d.ts", null)
    }

    @Test
    fun resolveModuleF() {
        resolve("@types/moduleF/lib/index.d.ts", "moduleF")
    }

    @Test
    fun resolveDeeplyNested() {
        resolve("deeply_nested/still_should_be_resolved/moduleE/some.d.ts", "moduleE")
    }

    @Test
    fun resolveModuleWithName() {
        resolve("whatever/path/we/pass.txt", "mylib", ConstNameResolver("mylib"))
    }

    private fun resolve(path: String, expected: String?, resolver: ModuleNameResolver = CommonJsNameResolver()) {
        val prefix = "./test/data/mode_nodules"
        val fullPath = File(prefix, path).absolutePath
        assertEquals(resolver.resolveName(fullPath), expected)
    }
}