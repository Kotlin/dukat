package org.jetbrains.dukat.tsmodel

interface NamedMethodLikeDeclaration : NamedMemberDeclaration, FunctionLikeDeclaration, WithModifiersDeclaration {
    val optional: Boolean
}