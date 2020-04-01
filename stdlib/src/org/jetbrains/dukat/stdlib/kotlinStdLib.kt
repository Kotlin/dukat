package org.jetbrains.dukat.stdlib

import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astCommon.startsWith

val KLIBROOT = IdentifierEntity("<KLIBROOT>")

fun NameEntity.isKotlinStdlibPrefixed(): Boolean {
    return startsWith(KLIBROOT)
}