package org.jetbrains.dukat.translator

import org.jetbrains.dukat.astModel.SourceSetModel

interface InputTranslator {
    fun translate(fileName: String): SourceSetModel
}