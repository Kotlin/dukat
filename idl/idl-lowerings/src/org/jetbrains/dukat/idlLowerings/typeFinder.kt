package org.jetbrains.dukat.idlLowerings

import org.jetbrains.dukat.idlDeclarations.IDLDictionaryDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLEnumDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLFileDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLInterfaceDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLSourceSetDeclaration

private enum class TypeKind {
    NOT_FOUND, INTERFACE, DICTIONARY, ENUM
}

private class TypeFinder(private val nameToFind: String): IDLLowering {
    var kind: TypeKind = TypeKind.NOT_FOUND

    override fun lowerInterfaceDeclaration(declaration: IDLInterfaceDeclaration, owner: IDLFileDeclaration): IDLInterfaceDeclaration {
        if (declaration.name == nameToFind) {
            kind = TypeKind.INTERFACE
        }
        return declaration
    }

    override fun lowerDictionaryDeclaration(declaration: IDLDictionaryDeclaration, owner: IDLFileDeclaration): IDLDictionaryDeclaration {
        if (declaration.name == nameToFind) {
            kind = TypeKind.DICTIONARY
        }
        return declaration
    }

    override fun lowerEnumDeclaration(declaration: IDLEnumDeclaration, owner: IDLFileDeclaration): IDLEnumDeclaration {
        if (declaration.name == nameToFind) {
            kind = TypeKind.ENUM
        }
        return declaration
    }
}

fun IDLSourceSetDeclaration.containsType(name: String): Boolean {
    val finder = TypeFinder(name)
    finder.lowerSourceSetDeclaration(this)
    return finder.kind != TypeKind.NOT_FOUND
}

fun IDLSourceSetDeclaration.containsEnum(name: String): Boolean {
    val finder = TypeFinder(name)
    finder.lowerSourceSetDeclaration(this)
    return finder.kind == TypeKind.ENUM
}