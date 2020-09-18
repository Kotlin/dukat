package org.jetbrains.dukat.ts.translator

import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.moduleNameResolver.ModuleNameResolver
import org.jetbrains.dukat.translator.InputTranslator

fun createJsByteArrayWithBodyTranslator(
    moduleNameResolver: ModuleNameResolver,
    packageName: NameEntity?
): InputTranslator<ByteArray> = JsRuntimeByteArrayTranslator(TypescriptLowerer(moduleNameResolver, packageName, true))