package org.jetbrains.dukat.tsLowerings

import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.ownerContext.NodeOwner
import org.jetbrains.dukat.tsmodel.ModuleDeclaration
import org.jetbrains.dukat.tsmodel.ParameterOwnerDeclaration
import org.jetbrains.dukat.tsmodel.SourceSetDeclaration
import org.jetbrains.dukat.tsmodel.types.TypeDeclaration
import org.jetbrains.dukat.tsmodel.types.UnionTypeDeclaration

private fun mapPrimitiveValue(value: String): String {
    return when (value) {
        "any" -> "Any"
        "boolean" -> "Boolean"
        "string" -> "String"
        "number" -> "Number"
        "Object" -> "Any"
        else -> value
    }
}

private fun NameEntity.mapPrimitive(): NameEntity {
    return when (this) {
        is IdentifierEntity -> {
            copy(value = mapPrimitiveValue(value))
        }
        else -> this
    }
}

private fun TypeDeclaration.isPrimitive(primitive: String): Boolean {
    return when (value) {
        is IdentifierEntity -> (value as IdentifierEntity).value == primitive
        else -> false
    }
}


private class PrimitivesLowering : DeclarationTypeLowering {
    override fun lowerTypeDeclaration(declaration: TypeDeclaration, owner: NodeOwner<ParameterOwnerDeclaration>?): TypeDeclaration {
        if (declaration.value == IdentifierEntity("Function")) {
            return declaration.copy(params = listOf(TypeDeclaration(IdentifierEntity("*"), emptyList())))
        }

        var value = declaration.value.mapPrimitive()
        var nullable = declaration.nullable
        var meta = declaration.meta

        if (declaration.isPrimitive("undefined") || declaration.isPrimitive("null")) {
            if (owner?.node !is UnionTypeDeclaration) {
                value = IdentifierEntity("Nothing")
                nullable = true
                meta = null
            }
        }

        return declaration.copy(
                value = value,
                params = declaration.params.map { lowerParameterValue(it, owner?.wrap(declaration)) },
                typeReference = if (value != declaration.value) {
                    null
                } else {
                    declaration.typeReference
                },
                nullable = nullable,
                meta = meta
        )
    }
}

private fun ModuleDeclaration.lowerPrimitives(): ModuleDeclaration {
    return PrimitivesLowering().lowerSourceDeclaration(this)
}

private fun SourceSetDeclaration.lowerPrimitives(): SourceSetDeclaration {
    return copy(sources = sources.map { it.copy(root = it.root.lowerPrimitives()) })
}

class LowerPrimitives : TsLowering {
    override fun lower(source: SourceSetDeclaration): SourceSetDeclaration {
        return source.lowerPrimitives()
    }
}