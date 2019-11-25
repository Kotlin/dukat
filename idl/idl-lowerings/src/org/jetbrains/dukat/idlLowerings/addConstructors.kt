package org.jetbrains.dukat.idlLowerings

import org.jetbrains.dukat.idlDeclarations.IDLConstructorDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLExtendedAttributeDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLFileDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLFunctionExtendedAttributeDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLInterfaceDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLNamedFunctionExtendedAttributeDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLSimpleExtendedAttributeDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLSingleTypeDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLSourceSetDeclaration
import org.jetbrains.dukat.idlDeclarations.InterfaceKind

private class ConstructorLowering : IDLLowering {

    private data class NamedConstructor(
            val interfaceName: String,
            val constructorName: String,
            val constructor: IDLConstructorDeclaration
    )

    private val namedConstructors: MutableSet<NamedConstructor> = mutableSetOf()

    private fun IDLExtendedAttributeDeclaration.convertToOrdinaryConstructor(): IDLConstructorDeclaration? {
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

    private fun IDLExtendedAttributeDeclaration.convertToNamedConstructor(interfaceName: String): NamedConstructor? {
        return when (this) {
            is IDLNamedFunctionExtendedAttributeDeclaration -> {
                if (name == "NamedConstructor") {
                    NamedConstructor(
                            interfaceName,
                            functionName,
                            IDLConstructorDeclaration(arguments)
                    )
                } else {
                    null
                }
            }
            else -> null
        }
    }

    private fun addNamedConstructors(declaration: IDLInterfaceDeclaration) {
        namedConstructors.addAll(declaration.extendedAttributes.mapNotNull {
            it.convertToNamedConstructor(declaration.name)
        })
    }


    private fun processOptionalConstructors(constructors: List<IDLConstructorDeclaration>): List<IDLConstructorDeclaration> {
        val (optionalConstructors, requiredConstructors) =
                constructors.partition { constructor ->
                    constructor.arguments.all { it.optional || it.defaultValue != null }
                }
        return if (optionalConstructors.size > 1) {
            val mainOptionalConstructor = optionalConstructors.maxBy { it.arguments.size }
            requiredConstructors + optionalConstructors.map { constructor ->
                if (constructor == mainOptionalConstructor) {
                    constructor
                } else {
                    constructor.copy(arguments = constructor.arguments.map {
                        it.copy(optional = false, defaultValue = null)
                    })
                }
            }
        } else {
            constructors
        }
    }

    override fun lowerInterfaceDeclaration(declaration: IDLInterfaceDeclaration, owner: IDLFileDeclaration): IDLInterfaceDeclaration {
        val ordinaryConstructors = processOptionalConstructors(declaration.extendedAttributes.mapNotNull { it.convertToOrdinaryConstructor() })
        addNamedConstructors(declaration)
        return when {
            ordinaryConstructors.size == 1 -> declaration.copy(
                    primaryConstructor = ordinaryConstructors[0]
            )
            else -> {
                val (emptyConstructors, nonEmptyConstructors) = ordinaryConstructors.partition { it.arguments.isEmpty() }
                when {
                    emptyConstructors.isNotEmpty() -> declaration.copy(
                            primaryConstructor = emptyConstructors.first(),
                            constructors = nonEmptyConstructors
                    )
                    else -> declaration.copy(
                            constructors = ordinaryConstructors
                    )
                }
            }
        }
    }

    override fun lowerFileDeclaration(fileDeclaration: IDLFileDeclaration): IDLFileDeclaration {
        var newFileDeclaration = super.lowerFileDeclaration(fileDeclaration)
        return newFileDeclaration.copy(
                declarations = newFileDeclaration.declarations + namedConstructors.map {
                    IDLInterfaceDeclaration(
                            name = it.constructorName,
                            attributes = listOf(),
                            operations = listOf(),
                            primaryConstructor = it.constructor,
                            constructors = listOf(),
                            parents = listOf(
                                    IDLSingleTypeDeclaration(it.interfaceName, null, false)
                            ),
                            unions = listOf(),
                            extendedAttributes = listOf(),
                            getters = listOf(),
                            setters = listOf(),
                            kind = InterfaceKind.INTERFACE,
                            callback = false,
                            generated = false,
                            partial = false,
                            mixin = false
                    )
                }
        ).also { namedConstructors.clear() }
    }
}

fun IDLSourceSetDeclaration.addConstructors(): IDLSourceSetDeclaration {
    return ConstructorLowering().lowerSourceSetDeclaration(this)
}