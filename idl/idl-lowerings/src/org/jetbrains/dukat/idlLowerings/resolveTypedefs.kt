package org.jetbrains.dukat.idlLowerings

import org.jetbrains.dukat.idlDeclarations.IDLFileDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLTypeDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLTypedefDeclaration

private class TypedefResolver(val context: TypedefContext) : IDLLowering {

    override fun lowerTypeDeclaration(declaration: IDLTypeDeclaration): IDLTypeDeclaration {
        val newType = context.resolveType(declaration)
        return newType ?: declaration
    }

}

private class TypedefContext : IDLLowering {
    private val typedefs: MutableMap<String, IDLTypeDeclaration> = mutableMapOf()

    fun registerTypedef(declaration: IDLTypedefDeclaration) {
        typedefs[declaration.name] = declaration.typeReference
    }

    fun resolveType(declaration: IDLTypeDeclaration): IDLTypeDeclaration? {
        return typedefs[declaration.name]
    }

    override fun lowerTypedefDeclaration(declaration: IDLTypedefDeclaration): IDLTypedefDeclaration {
        registerTypedef(declaration)
        return declaration
    }
}

fun IDLFileDeclaration.resolveTypedefs(): IDLFileDeclaration {
    val context = TypedefContext()
    return TypedefResolver(context).lowerFileDeclaration(context.lowerFileDeclaration(this))
}