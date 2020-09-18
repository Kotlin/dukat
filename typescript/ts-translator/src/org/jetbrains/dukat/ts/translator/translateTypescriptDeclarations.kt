package org.jetbrains.dukat.ts.translator

import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astModel.SourceSetModel
import org.jetbrains.dukat.model.serialization.readSourceSetFromFile
import org.jetbrains.dukat.moduleNameResolver.ModuleNameResolver

fun translateTypescriptDeclarations(data: ByteArray, moduleResolver: ModuleNameResolver, packageName: NameEntity?, addSuppressAnnotations: Boolean, kotlinStdLibPath: String? = null): SourceSetModel {
    val translator = JsRuntimeByteArrayTranslator(TypescriptLowerer(moduleResolver, packageName, addSuppressAnnotations, kotlinStdLibPath?.let { readSourceSetFromFile(kotlinStdLibPath) }))
    return translator.translate(data)
}