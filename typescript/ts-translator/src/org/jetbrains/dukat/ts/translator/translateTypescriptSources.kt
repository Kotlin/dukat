package org.jetbrains.dukat.ts.translator

import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astModel.SourceSetModel
import org.jetbrains.dukat.moduleNameResolver.ModuleNameResolver

fun translateTypescriptSources(data: ByteArray, moduleResolver: ModuleNameResolver, basePackageName: NameEntity?): SourceSetModel {
    val translator = createJsByteArrayWithBodyTranslator(moduleResolver, basePackageName)
    return translator.translate(data)
}