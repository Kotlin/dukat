package org.jetbrains.dukat.ts.translator

import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.moduleNameResolver.ModuleNameResolver

fun createJsByteArrayTranslator(moduleNameResolver: ModuleNameResolver, packageName: NameEntity?) = JsRuntimeByteArrayTranslator(moduleNameResolver, packageName)