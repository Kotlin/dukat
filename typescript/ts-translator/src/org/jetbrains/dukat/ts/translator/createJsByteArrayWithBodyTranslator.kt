package org.jetbrains.dukat.ts.translator

import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.moduleNameResolver.ModuleNameResolver

fun createJsByteArrayWithBodyTranslator(
    moduleNameResolver: ModuleNameResolver,
    packageName: NameEntity?
) = JsRuntimeByteArrayTranslator(TypescriptWithBodyLowerer(moduleNameResolver, packageName))