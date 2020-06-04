package org.jetbrains.dukat.tsmodel

data class ArrayDestructuringDeclaration(
    val elements: List<BindingElementDeclaration>
) : VariableLikeDeclaration, BindingElementDeclaration