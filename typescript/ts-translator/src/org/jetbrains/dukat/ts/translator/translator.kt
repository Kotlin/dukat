package org.jetbrains.dukat.ts.translator

import org.jetbrains.dukat.moduleNameResolver.ModuleNameResolver

fun createJsFileTranslator(moduleNameResolver: ModuleNameResolver, translatorPath: String, libPath: String, nodePath: String) = JsRuntimeFileTranslator(moduleNameResolver, translatorPath, libPath, nodePath)
fun createJsByteArrayTranslator(moduleNameResolver: ModuleNameResolver) = JsRuntimeByteArrayTranslator(moduleNameResolver)