package org.jetbrains.dukat.ast.model.nodes

import org.jetbrains.dukat.tsmodel.ParameterDeclaration

interface ParameterOwnerNode {
    val parameters: List<ParameterDeclaration>
}