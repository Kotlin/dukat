package org.jetbrains.dukat.ast.model.nodes.export

import org.jetbrains.dukat.astCommon.NameEntity

sealed class ExportQualifier

data class JsModule(val name: NameEntity?, val qualifier: NameEntity? = null) : ExportQualifier()
object JsDefault : ExportQualifier()