package org.jetbrains.dukat.tsmodel

data class ConstructorDeclaration(
        val parameters: List<ConstructorParameterDeclaration>,
        val typeParameters: List<TypeParameterDeclaration>,
        override val modifiers: Set<ModifierDeclaration>,
        val body: BlockDeclaration?
) : MemberDeclaration, ParameterOwnerDeclaration, WithModifiersDeclaration