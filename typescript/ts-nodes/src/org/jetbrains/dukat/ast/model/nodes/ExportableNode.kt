package org.jetbrains.dukat.ast.model.nodes

import org.jetbrains.dukat.tsmodel.ExportQualifier

interface ExportableNode : UniqueNode {
    val exportQualifier: ExportQualifier?
}