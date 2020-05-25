package org.jetbrains.dukat.compiler.tests

interface MethodSource {
    fun fileSetWithDescriptors(): Array<Array<String>>
}

operator fun MethodSource.plus(b: MethodSource): MethodSource {
    return object  : MethodSource {
        override fun fileSetWithDescriptors(): Array<Array<String>> {
            return fileSetWithDescriptors() + b.fileSetWithDescriptors()
        }
    }
}