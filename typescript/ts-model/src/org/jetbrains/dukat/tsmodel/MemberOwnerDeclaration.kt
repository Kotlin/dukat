package org.jetbrains.dukat.tsmodel

interface MemberOwnerDeclaration : Declaration {
    val members: List<MemberDeclaration>
}