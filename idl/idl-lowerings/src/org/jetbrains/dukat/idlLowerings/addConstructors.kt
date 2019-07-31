package org.jetbrains.dukat.idlLowerings

import org.jetbrains.dukat.idlDeclarations.IDLConstructorDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLExtendedAttributeDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLFileDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLFunctionExtendedAttributeDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLInterfaceDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLSimpleExtendedAttributeDeclaration

private class ConstructorLowering : IDLLowering {

    private fun IDLExtendedAttributeDeclaration.convertToConstructor(): IDLConstructorDeclaration? {
        return when (this) {
            is IDLFunctionExtendedAttributeDeclaration -> {
                if (functionName == "Constructor") {
                    IDLConstructorDeclaration(arguments)
                } else {
                    null
                }
            }
            is IDLSimpleExtendedAttributeDeclaration -> {
                if (attributeName == "Constructor") {
                    IDLConstructorDeclaration(listOf())
                } else {
                    null
                }
            }
            else -> null
        }
    }

    override fun lowerInterfaceDeclaration(declaration: IDLInterfaceDeclaration): IDLInterfaceDeclaration {
        val constructors = declaration.extendedAttributes.mapNotNull { it.convertToConstructor() }
        return if (constructors.size == 1) {
            declaration.copy(primaryConstructor = constructors[0])
        } else {
            declaration.copy(constructors = constructors)
        }
    }
}

fun IDLFileDeclaration.addConstructors(): IDLFileDeclaration {
    return ConstructorLowering().lowerFileDeclaration(this)
}