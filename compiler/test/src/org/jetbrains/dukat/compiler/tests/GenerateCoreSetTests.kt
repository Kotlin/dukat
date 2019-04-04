package org.jetbrains.dukat.compiler.tests

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.io.File

class GenerateCoreSetTests : StandardTests() {

    @DisplayName("core test set")
    @ParameterizedTest(name = "{0}")
    @MethodSource("coreSet")
    fun withValueSource(name: String, tsPath: String, ktPath: String) {
        assertContentEquals(name, tsPath, ktPath)
    }

//    @DisplayName("core test set compile")
//    @ParameterizedTest(name = "{0}")
//    @MethodSource("coreSet")
//    fun withValueSourceCompiled(name: String, tsPath: String, ktPath: String) {
//        assertContentCompiles(name, tsPath)
//    }

    companion object {
        fun fileSet(path: String): Array<Array<String>> {
            val d_ts_postfix = ".d.ts"

            val rootFolder = File(path)
            return rootFolder.walk().filter { file ->
                file.path.endsWith(d_ts_postfix)
            }.map { file ->
                val tsPath = file.absolutePath
                val ktPath = tsPath.replace(d_ts_postfix, ".d.kt")
                val descriptor = file.relativeTo(rootFolder).path.replace(path, "").replace(d_ts_postfix, "")

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