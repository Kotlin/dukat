package org.jetbrains.dukat.idlLowerings

import org.jetbrains.dukat.idlDeclarations.IDLFileDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLTypeDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLTypedefDeclaration

private class TypedefResolver(val context: TypedefContext) : IDLLowering {

    override fun lowerTypeDeclaration(declaration: IDLTypeDeclaration): IDLTypeDeclaration {
        return declaration.copy(
                typeParameter = declaration.typeParameter?.let { lowerTypeDeclaration(it) },
                name = context.resolveType(declaration) ?: declaration.name
        )
    }

}

private class TypedefContext : IDLLowering {
    private val typedefs: MutableMap<String, String> = mutableMapOf()

    fun registerTypedef(declaration: IDLTypedefDeclaration) {
        typedefs[declaration.name] = declaration.typeReference.name
    }

    fun resolveType(declaration: IDLTypeDeclaration): String? {
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