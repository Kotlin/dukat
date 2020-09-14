package org.jetbrains.dukat.ts.translator

import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astModel.SourceSetModel
import org.jetbrains.dukat.moduleNameResolver.ModuleNameResolver

fun translateTypescriptDeclarations(data: ByteArray, moduleResolver: ModuleNameResolver, packageName: NameEntity?): SourceSetModel {
    val translator = JsRuntimeByteArrayTranslator(TypescriptLowerer(moduleResolver, packageName, true))
    return translator.translate(data)
}