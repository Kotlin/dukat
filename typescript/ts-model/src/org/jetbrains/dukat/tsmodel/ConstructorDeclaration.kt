package org.jetbrains.dukat.tsmodel

data class ConstructorDeclaration(
        val parameters: List<ParameterDeclaration>,
        val typeParameters: List<TypeParameterDeclaration>,
        val modifiers: List<ModifierDeclaration>,
        val body: BlockDeclaration?
) : MemberDeclaration, ParameterOwnerDeclaration