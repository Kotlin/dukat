package org.jetbrains.dukat.idlDeclarations

interface IDLTypeDeclaration : IDLDeclaration {
    val name: String
    val nullable: Boolean
}