package org.jetbrains.dukat.stdlib

import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astCommon.leftMost

val TSLIBROOT = IdentifierEntity("<LIBROOT>")

fun NameEntity.isTsStdlibPrefixed(): Boolean {
    return leftMost() == TSLIBROOT
}