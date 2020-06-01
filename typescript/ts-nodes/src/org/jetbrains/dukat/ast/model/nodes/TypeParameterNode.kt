package org.jetbrains.dukat.ast.model.nodes

import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration

data class TypeParameterNode(
        val name: NameEntity,

        override var nullable: Boolean = false,
        override var meta: ParameterValueDeclaration? = null
) : TypeNode