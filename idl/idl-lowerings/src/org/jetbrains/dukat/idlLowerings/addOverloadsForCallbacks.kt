package org.jetbrains.dukat.idlLowerings

import org.jetbrains.dukat.idlDeclarations.IDLArgumentDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLFileDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLFunctionTypeDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLInterfaceDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLOperationDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLSourceSetDeclaration

private fun <T> allSubsets(list: List<T>): List<List<T>> {
    return when {
        list.isEmpty() -> listOf(listOf())
        else -> allSubsets(list.dropLast(1)).let { subsetsWithoutLast ->
            subsetsWithoutLast + subsetsWithoutLast.map { it + list.last() }
        }
    }
}

private class CallbackOverloadResolver(val context: CallbackContext) : IDLLowering {

    private fun IDLOperationDeclaration.toFunctionType(nullable: Boolean, comment: String?): IDLFunctionTypeDeclaration {
        return IDLFunctionTypeDeclaration(
                name = name,
                returnType = returnType,
                arguments = arguments.map { it.copy(name = "") },
                nullable = nullable,
                comment = comment
        )
    }

    private fun IDLArgumentDeclaration.toFunctionType(): IDLArgumentDeclaration {
        val callback = context.getCallback(type.name)!!
        return copy(type = callback.operations[0].toFunctionType(type.nullable, type.comment))
    }

    private fun IDLOperationDeclaration.generateOverloads(): List<IDLOperationDeclaration> {
        val callbackTypeArguments = arguments.filter {
            context.getCallback(it.type.name) != null
        }
        return allSubsets(callbackTypeArguments).map { argumentsToReplace ->
            copy(arguments = arguments.toMutableList().map { argument ->
                if (argument in argumentsToReplace) {
                    argument.toFunctionType()
                } else {
                    argument
                }
            })
        }
    }

    override fun lowerInterfaceDeclaration(declaration: IDLInterfaceDeclaration, owner: IDLFileDeclaration): IDLInterfaceDeclaration {
        return declaration.copy(
                operations = declaration.operations.flatMap { it.generateOverloads() }
        )
    }
}

private class CallbackContext : IDLLowering {
    private val callbacks: MutableMap<String, IDLInterfaceDeclaration> = mutableMapOf()

    override fun lowerInterfaceDeclaration(declaration: IDLInterfaceDeclaration, owner: IDLFileDeclaration): IDLInterfaceDeclaration {
        if (declaration.callback) {
            callbacks[declaration.name] = declaration
        }
        return declaration
    }

    fun getCallback(name: String): IDLInterfaceDeclaration? {
        return callbacks[name]
    }
}

fun IDLSourceSetDeclaration.addOverloadsForCallbacks(): IDLSourceSetDeclaration {
    val context = CallbackContext()
    return CallbackOverloadResolver(context).lowerSourceSetDeclaration(
            context.lowerSourceSetDeclaration(this)
    )
}