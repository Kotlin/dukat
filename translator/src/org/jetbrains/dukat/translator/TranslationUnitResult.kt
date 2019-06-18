package org.jetbrains.dukat.translator

import org.jetbrains.dukat.astCommon.NameEntity

sealed class TranslationUnitResult

data class ModuleTranslationUnit(
        val fileName: String,
        val packageName: NameEntity,
        val content: String
) : TranslationUnitResult()


data class TranslationErrorInvalidFile(val fileName: String) : TranslationUnitResult()
data class TranslationErrorFileNotFound(val fileName: String) : TranslationUnitResult()