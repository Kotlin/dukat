package org.jetbrains.dukat.translator

import org.jetbrains.dukat.astCommon.IdentifierEntity

val TS_STDLIB_WHITE_LIST = setOf(
        IdentifierEntity("TsStdLib_Iterator"),
        IdentifierEntity("IteratorResult"),
        IdentifierEntity("IterableIterator"),
        IdentifierEntity("WeakMap"),
        IdentifierEntity("WeakMapConstructor"),
        IdentifierEntity("WeakSet"),
        IdentifierEntity("WeakSetConstructor"),
        IdentifierEntity("SymbolConstructor")
)