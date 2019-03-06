package org.jetbrains.dukat.compiler.tests

import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.io.File

class AllTestsCore : StandardTests() {
    @ParameterizedTest(name="{0}")
    @MethodSource("coreSet")
    fun withValueSource(name: String, tsPath: String, ktPath: String) {
        assertContentEquals(name, tsPath, ktPath)
    }

    companion object {
        fun fileSet(path: String): Array<Array<String>> {
            val rootFolder = File(path)
            return rootFolder.walk().filter { file ->
                file.path.endsWith(".d.ts")
            }.map { file ->
                val tsPath = file.absolutePath
                val ktPath = tsPath.replace(".d.ts", ".d.kt")
                val descriptor = file.relativeTo(rootFolder).path.replace(path, "").replace(".d.ts", "")

                arrayOf(
                    descriptor,
                    tsPath,
                    ktPath
                )
            }.toList().toTypedArray()
        }

        @JvmStatic
        fun coreSet(): Array<Array<String>> {
            return fileSet("./test/data")
        }

    }
}