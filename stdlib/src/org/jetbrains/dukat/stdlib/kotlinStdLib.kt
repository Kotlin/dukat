package org.jetbrains.dukat.stdlib

import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astCommon.hasPrefix

val KLIBROOT = IdentifierEntity("kotlin")

fun NameEntity.isKotlinStdlibPrefixed(): Boolean {
    return hasPrefix(KLIBROOT)
}