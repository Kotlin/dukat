package org.jetbrains.dukat.ast.model.nodes

import org.jetbrains.dukat.ast.model.declaration.ClassLikeDeclaration
import org.jetbrains.dukat.ast.model.declaration.MemberDeclaration
import org.jetbrains.dukat.ast.model.declaration.TypeParameterDeclaration
import org.jetbrains.dukat.ast.model.declaration.types.ParameterValueDeclaration

data class PropertyNode(
        val name: String,
        val type: ParameterValueDeclaration,
        val typeParameters: List<TypeParameterDeclaration>,

        val owner: ClassLikeDeclaration,
        val static: Boolean,
        val override: Boolean,

        val getter: Boolean,
        val setter: Boolean
) : MemberDeclaration