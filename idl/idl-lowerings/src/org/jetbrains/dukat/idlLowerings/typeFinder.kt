package org.jetbrains.dukat.idlLowerings

import org.jetbrains.dukat.idlDeclarations.IDLDictionaryDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLFileDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLInterfaceDeclaration

private class TypeFinder(private val nameToFind: String): IDLLowering {
    var found: Boolean = false

    override fun lowerInterfaceDeclaration(declaration: IDLInterfaceDeclaration): IDLInterfaceDeclaration {
        if (declaration.name == nameToFind) {
            found = true
        }
        return declaration
    }

    override fun lowerDictionaryDeclaration(declaration: IDLDictionaryDeclaration): IDLDictionaryDeclaration {
        if (declaration.name == nameToFind) {
            found = true
        }
        return declaration
    }
}

fun IDLFileDeclaration.containsInterface(name: String): Boolean {
    val finder = TypeFinder(name)
    finder.lowerFileDeclaration(this)
    return finder.found
}