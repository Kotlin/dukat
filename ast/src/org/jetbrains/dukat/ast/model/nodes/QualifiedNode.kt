package org.jetbrains.dukat.ast.model.nodes

import org.jetbrains.dukat.tsmodel.ModuleReferenceDeclaration
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration

data class QualifiedNode(
    val left: QualifiedLeftNode,
    val right: IdentifierNode,

    override var nullable: Boolean = false,
    override var meta: ParameterValueDeclaration? = null
) : ParameterValueDeclaration, ModuleReferenceDeclaration, QualifiedLeftNode, TypeNodeValue