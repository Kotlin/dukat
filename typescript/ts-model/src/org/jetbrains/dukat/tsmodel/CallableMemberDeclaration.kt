package org.jetbrains.dukat.tsmodel

interface CallableMemberDeclaration : ParameterOwnerDeclaration, MemberDeclaration {
    val parameters: List<ParameterDeclaration>
}