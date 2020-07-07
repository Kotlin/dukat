package org.jetbrains.dukat.tsmodel

data class ConstructorDeclaration(
        override val parameters: List<ParameterDeclaration>,
        val typeParameters: List<TypeParameterDeclaration>,
        override val modifiers: Set<ModifierDeclaration>,
        val body: BlockDeclaration?
) : MethodDeclaration, WithModifiersDeclaration