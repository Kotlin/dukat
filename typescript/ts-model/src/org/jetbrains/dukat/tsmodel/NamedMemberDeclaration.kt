package org.jetbrains.dukat.tsmodel

interface NamedMemberDeclaration : MemberDeclaration {
    val name: String
}