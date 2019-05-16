package org.jetbrains.dukat.ast.model.nodes

import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.tsmodel.ModuleReferenceDeclaration

data class GenericIdentifierNode(
        val value: String,
        val typeParameters: List<TypeValueNode>
) : ModuleReferenceDeclaration, NameEntity, StatementNode