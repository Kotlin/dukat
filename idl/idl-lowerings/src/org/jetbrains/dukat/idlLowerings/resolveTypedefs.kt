package org.jetbrains.dukat.idlLowerings

import org.jetbrains.dukat.idlDeclarations.IDLFileDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLFunctionTypeDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLSingleTypeDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLTypeDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLTypedefDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLUnionTypeDeclaration

private class TypedefResolver(val context: TypedefContext) : IDLLowering {

    override fun lowerTypeDeclaration(declaration: IDLTypeDeclaration): IDLTypeDeclaration {
        return when (declaration) {
            is IDLUnionTypeDeclaration -> declaration.copy(unionMembers = declaration.unionMembers.map { lowerTypeDeclaration(it) })
            is IDLSingleTypeDeclaration -> {
                var newType = context.resolveType(declaration)
                if (newType is IDLSingleTypeDeclaration && newType != context.resolveType(newType)) {
                    newType = lowerTypeDeclaration(newType)
                }
                when (newType) {
                    is IDLSingleTypeDeclaration -> {
                        newType.copy(
                                typeParameter = declaration.typeParameter?.let { lowerTypeDeclaration(it) }
                                        ?: newType.typeParameter?.let { lowerTypeDeclaration(it) },
                                nullable = declaration.nullable || newType.nullable
                        )
                    }
                    is IDLUnionTypeDeclaration -> newType.copy(
                            unionMembers = newType.unionMembers.map { lowerTypeDeclaration(it) },
                            name = declaration.name,
                            nullable = declaration.nullable
                    )
                    is IDLFunctionTypeDeclaration -> newType.copy(
                            nullable = declaration.nullable || newType.nullable
                    )
                    else -> declaration
                }
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