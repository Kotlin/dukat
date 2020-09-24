package org.jetbrains.dukat.tsmodel

import org.jetbrains.dukat.astCommon.NameEntity

sealed class ExportQualifier

data class JsModule(val name: String?, val qualifier: Boolean = false) : ExportQualifier()
object JsDefault : ExportQualifier()