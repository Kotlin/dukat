package org.jetbrains.dukat.stdlib.org.jetbrains.dukat.stdlib
import org.jetbrains.dukat.astCommon.IdentifierEntity

val TS_STDLIB_WHITE_LIST = setOf(
        IdentifierEntity("TsStdLib_Iterator"),
        IdentifierEntity("ErrorConstructor"),
        IdentifierEntity("IteratorResult"),
        IdentifierEntity("IterableIterator"),
        IdentifierEntity("WeakMap"),
        IdentifierEntity("WeakMapConstructor"),
        IdentifierEntity("WeakSet"),
        IdentifierEntity("WeakSetConstructor"),
        IdentifierEntity("SymbolConstructor")
)