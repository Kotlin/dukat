package org.jetbrains.dukat.js.translator

import org.jetbrains.dukat.astModel.SourceSetModel
import org.jetbrains.dukat.moduleNameResolver.ModuleNameResolver
import org.jetbrains.dukat.ts.translator.JsRuntimeByteArrayTranslator

fun translateJsSources(data: ByteArray, moduleResolver: ModuleNameResolver): SourceSetModel {
    val translator = JsRuntimeByteArrayTranslator(JavaScriptLowerer(moduleResolver))
    return translator.translate(data)
}