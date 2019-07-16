package org.jetbrains.dukat.idlLowerings

import org.jetbrains.dukat.idlDeclarations.*


interface IDLLowering {
    fun lowerInterfaceDeclaration(declaration: IDLInterfaceDeclaration): IDLInterfaceDeclaration

    fun lowerTopLevelDeclaration(declaration: IDLTopLevelDeclaration): IDLTopLevelDeclaration {
        return when (declaration) {
            is IDLInterfaceDeclaration -> lowerInterfaceDeclaration(declaration)
            else -> declaration
        }
    }

    fun lowerTopLevelDeclarations(declarations: List<IDLTopLevelDeclaration>): List<IDLTopLevelDeclaration> {
        return declarations.map { declaration ->
            lowerTopLevelDeclaration(declaration)
        }
    }

    fun lowerFileDeclaration(fileDeclaration: IDLFileDeclaration): IDLFileDeclaration {
        return fileDeclaration.copy(
                declarations = lowerTopLevelDeclarations(fileDeclaration.declarations)
        )
    }
}