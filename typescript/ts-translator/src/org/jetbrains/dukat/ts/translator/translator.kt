package org.jetbrains.dukat.ts.translator

import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.moduleNameResolver.ModuleNameResolver

fun createJsFileTranslator(packageName: NameEntity, moduleNameResolver: ModuleNameResolver, translatorPath: String, libPath: String, nodePath: String) = JsRuntimeFileTranslator(packageName, moduleNameResolver, translatorPath, libPath, nodePath)
fun createJsByteArrayTranslator(packageName: NameEntity, moduleNameResolver: ModuleNameResolver) = JsRuntimeByteArrayTranslator(packageName, moduleNameResolver)