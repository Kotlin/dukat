package org.jetbrains.dukat.tsmodel

interface MethodDeclaration : ParameterOwnerDeclaration, MemberDeclaration {
    val parameters: List<ParameterDeclaration>
}