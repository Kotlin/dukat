package org.jetbrains.dukat.stdlib

import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astCommon.startsWith

val TSLIBROOT = IdentifierEntity("tsstdlib")

fun NameEntity.isTsStdlibPrefixed(): Boolean {
    return startsWith(TSLIBROOT)
}