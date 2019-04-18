package org.jetbrains.dukat.compiler.tests

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.io.File


abstract class GenerateCoreSetTests : StandardTests() {

    @DisplayName("core test set")
    @ParameterizedTest(name = "{0}")
    @MethodSource("coreSet")
    fun withValueSource(name: String, tsPath: String, ktPath: String) {
        assertContentEquals(name, tsPath, ktPath)
    }

    companion object {
        val TS_POSTFIX = ".d.ts"

        fun fileSet(path: String, postfix: String, recursive: Boolean = false): Sequence<File> {
            val rootFolder = File(path)

            val files = if (recursive) rootFolder.walk() else rootFolder.walkTopDown()

            return files.filter { file ->
                file.path.endsWith(postfix)
            }
        }

        fun fileSetWithDescriptors(path: String, recursive: Boolean = false): Array<Array<String>> {
            val rootFolder = File(path)
            return fileSet(path, TS_POSTFIX, recursive).map { file ->
                val tsPath = file.absolutePath
                val ktPath = tsPath.replace(TS_POSTFIX, ".d.kt")
                val descriptor = file.relativeTo(rootFolder).path.replace(path, "").replace(TS_POSTFIX, "")

                arrayOf(
                        descriptor,
                        tsPath,
                        ktPath
                )
            }.toList().toTypedArray()
        }

        @JvmStatic
        fun coreSet(): Array<Array<String>> {
            return fileSetWithDescriptors("./test/data")
        }

        @JvmStatic
        fun extendedSet(): Array<Array<String>> {
            return fileSetWithDescriptors("./build/DefinitelyTyped")
        }
    }
}