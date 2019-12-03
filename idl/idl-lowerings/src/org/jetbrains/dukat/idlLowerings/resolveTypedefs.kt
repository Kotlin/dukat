package org.jetbrains.dukat.idlLowerings

import org.jetbrains.dukat.idlDeclarations.IDLFileDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLFunctionTypeDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLSingleTypeDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLSourceSetDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLTypeDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLTypedefDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLUnionTypeDeclaration

private class TypedefResolver(val context: TypedefContext) : IDLLowering {

    override fun lowerTypeDeclaration(declaration: IDLTypeDeclaration, owner: IDLFileDeclaration): IDLTypeDeclaration {
        return when (declaration) {
            is IDLUnionTypeDeclaration -> declaration.copy(unionMembers = declaration.unionMembers.map { lowerTypeDeclaration(it, owner) })
            is IDLSingleTypeDeclaration -> {
                var newType = context.resolveType(declaration)
                if (newType is IDLSingleTypeDeclaration && newType != context.resolveType(newType)) {
                    newType = lowerTypeDeclaration(newType, owner)
                }
                when (newType) {
                    is IDLSingleTypeDeclaration -> {
                        newType.copy(
                                typeParameter = declaration.typeParameter?.let { lowerTypeDeclaration(it, owner) }
                                        ?: newType.typeParameter?.let { lowerTypeDeclaration(it, owner) },
                                nullable = declaration.nullable || newType.nullable,
                                comment = declaration.comment
                        )
                    }
                    is IDLUnionTypeDeclaration -> newType.copy(
                            unionMembers = newType.unionMembers.map { lowerTypeDeclaration(it, owner) },
                            name = declaration.name,
                            nullable = declaration.nullable,
                            comment = declaration.comment
                    )
                    is IDLFunctionTypeDeclaration -> newType.copy(
                            nullable = declaration.nullable || newType.nullable,
                            returnType = lowerTypeDeclaration(newType.returnType, owner),
                            arguments = newType.arguments.map { lowerArgumentDeclaration(it, owner) },
                            comment = declaration.comment
                    )
                    else -> declaration
                }
            }
            is IDLFunctionTypeDeclaration -> declaration.copy(
                    returnType = lowerTypeDeclaration(declaration.returnType, owner),
                    arguments = declaration.arguments.map { lowerArgumentDeclaration(it, owner) }
            )
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

    override fun lowerTypedefDeclaration(declaration: IDLTypedefDeclaration, owner: IDLFileDeclaration): IDLTypedefDeclaration {
        registerTypedef(declaration)
        return declaration
    }
}

fun IDLSourceSetDeclaration.resolveTypedefs(): IDLSourceSetDeclaration {
    val context = TypedefContext()
    return TypedefResolver(context).lowerSourceSetDeclaration(
            context.lowerSourceSetDeclaration(this)
    )
}