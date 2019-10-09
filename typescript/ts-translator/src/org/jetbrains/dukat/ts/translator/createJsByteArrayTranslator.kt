package org.jetbrains.dukat.ts.translator

import org.jetbrains.dukat.moduleNameResolver.ModuleNameResolver

fun createJsByteArrayTranslator(moduleNameResolver: ModuleNameResolver) = JsRuntimeByteArrayTranslator(moduleNameResolver)