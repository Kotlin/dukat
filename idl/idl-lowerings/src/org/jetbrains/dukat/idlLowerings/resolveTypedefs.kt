package org.jetbrains.dukat.idlLowerings

import org.jetbrains.dukat.idlDeclarations.*

private class TypedefResolver(val context: TypedefContext) : IDLLowering {

    override fun lowerTypeDeclaration(declaration: IDLTypeDeclaration): IDLTypeDeclaration {
        return when (declaration) {
            is IDLUnionTypeDeclaration -> declaration.copy(unionMembers = declaration.unionMembers.map { lowerTypeDeclaration(it) })
            is IDLSingleTypeDeclaration -> when (val newType = context.resolveType(declaration)) {
                is IDLSingleTypeDeclaration -> newType.copy(
                        typeParameter = declaration.typeParameter?.let { lowerTypeDeclaration(it) },
                        nullable = declaration.nullable
                )
                is IDLUnionTypeDeclaration -> newType.copy(
                        unionMembers = newType.unionMembers.map { lowerTypeDeclaration(it) },
                        name = declaration.name,
                        nullable = declaration.nullable
                )
                else -> declaration
            }
            else -> declaration
        }
    }
}

private class TypedefContext : IDLLowering {
    private val typedefs: MutableMap<String, IDLTypeDeclaration> = mutableMapOf()

    fun registerTypedef(declaration: IDLTypedefDeclaration) {
        typedefs[declaration.name] = declaration.typeReference
    }

    fun resolveType(declaration: IDLSingleTypeDeclaration): IDLTypeDeclaration {
        return if (typedefs.containsKey(declaration.name)) {
            typedefs[declaration.name]!!
        } else {
            declaration
        }
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