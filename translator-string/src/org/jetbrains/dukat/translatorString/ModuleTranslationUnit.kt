package org.jetbrains.dukat.translatorString

import org.jetbrains.dukat.astCommon.NameEntity

data class ModuleTranslationUnit(
        val fileName: String,
        val packageName: NameEntity,
        val content: String
)