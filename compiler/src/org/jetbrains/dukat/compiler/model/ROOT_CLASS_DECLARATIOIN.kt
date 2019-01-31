package org.jetbrains.dukat.compiler.model

import org.jetbrains.dukat.tsmodel.ClassLikeDeclaration
import org.jetbrains.dukat.tsmodel.TypeParameterDeclaration

object ROOT_CLASS_DECLARATION : ClassLikeDeclaration {
    override val name: String = "ROOT_CLASS_DECLARATION"
    override val typeParameters: List<TypeParameterDeclaration> = emptyList()
}