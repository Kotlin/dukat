package org.jetbrains.dukat.ast.model.nodes

import org.jetbrains.dukat.ast.model.nodes.export.ExportQualifier

interface ExportableNode : UniqueNode {
    val exportQualifier: ExportQualifier?
}