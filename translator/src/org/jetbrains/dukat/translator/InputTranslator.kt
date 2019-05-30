package org.jetbrains.dukat.translator

import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astModel.SourceSetModel

interface InputTranslator {
    fun translate(fileName: String, packageName: NameEntity): SourceSetModel
}