package org.jetbrains.dukat.ast.model.nodes

import org.jetbrains.dukat.tsmodel.MemberDeclaration
import org.jetbrains.dukat.tsmodel.expression.ExpressionDeclaration
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration
import org.jetbrains.dukat.tsmodel.types.TypeDeclaration

data class PropertyNode(
        val name: String,
        val type: ParameterValueDeclaration,
        val typeParameters: List<TypeDeclaration>,

        val static: Boolean,

        val initializer: ExpressionDeclaration?,

        val getter: Boolean,
        val setter: Boolean,

        val explicitlyDeclaredType: Boolean,

        val lateinit: Boolean
) : MemberDeclaration