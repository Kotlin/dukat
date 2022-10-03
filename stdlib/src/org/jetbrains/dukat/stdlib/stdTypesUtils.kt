 package org.jetbrains.dukat.stdlib

import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astCommon.QualifierEntity

val NameEntity.isDynamic: Boolean
    get() = when (this) {
        is QualifierEntity -> isTsStdlibPrefixed() && right.value == "dynamic"
        is IdentifierEntity -> this.value == "dynamic"
    }
