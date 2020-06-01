package org.jetbrains.dukat.ast.model.nodes

import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astCommon.ReferenceEntity
import org.jetbrains.dukat.tsmodel.TypeParameterDeclaration
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration

data class GeneratedInterfaceReferenceNode(
        val name: NameEntity,
        val typeParameters: List<TypeParameterDeclaration>,
        val reference: ReferenceEntity?,

        override val nullable: Boolean = false,
        override var meta: ParameterValueDeclaration? = null
) : TypeNode