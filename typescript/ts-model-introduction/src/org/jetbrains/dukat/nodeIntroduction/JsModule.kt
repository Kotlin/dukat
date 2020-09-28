package org.jetbrains.dukat.nodeIntroduction

import org.jetbrains.dukat.astCommon.NameEntity

data class JsModule(val name: NameEntity?, val qualifier: Boolean = false)
