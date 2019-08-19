package org.jetbrains.dukat.translator

import org.jetbrains.dukat.astModel.SourceBundleModel

interface InputTranslator<T> {
    fun translate(data: T): SourceBundleModel
}