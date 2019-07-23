package org.jetbrains.dukat.idlLowerings

import org.jetbrains.dukat.idlDeclarations.IDLFileDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLInterfaceDeclaration

private class InterfaceFinder(private val interfaceName: String): IDLLowering {
    var found: Boolean = false

    override fun lowerInterfaceDeclaration(declaration: IDLInterfaceDeclaration): IDLInterfaceDeclaration {
        if (declaration.name == interfaceName) {
            found = true
        }
        return declaration
    }
}

fun IDLFileDeclaration.containsInterface(name: String): Boolean {
    val finder = InterfaceFinder(name)
    finder.lowerFileDeclaration(this)
    return finder.found
}