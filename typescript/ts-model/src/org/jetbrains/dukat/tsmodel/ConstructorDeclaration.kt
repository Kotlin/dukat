package org.jetbrains.dukat.tsmodel

data class ConstructorDeclaration(
        val parameters: List<ParameterDeclaration>,
        val typeParameters: List<TypeParameterDeclaration>,
        override val modifiers: Set<ModifierDeclaration>,
        val body: BlockDeclaration?
) : MemberDeclaration, ParameterOwnerDeclaration, WithModifiersDeclaration