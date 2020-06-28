package org.jetbrains.dukat.tsLowerings

import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.ownerContext.NodeOwner
import org.jetbrains.dukat.panic.raiseConcern
import org.jetbrains.dukat.tsmodel.GeneratedInterfaceReferenceDeclaration
import org.jetbrains.dukat.tsmodel.ParameterOwnerDeclaration
import org.jetbrains.dukat.tsmodel.SourceSetDeclaration
import org.jetbrains.dukat.tsmodel.types.FunctionTypeDeclaration
import org.jetbrains.dukat.tsmodel.types.IntersectionTypeDeclaration
import org.jetbrains.dukat.tsmodel.types.NumericLiteralDeclaration
import org.jetbrains.dukat.tsmodel.types.ObjectLiteralDeclaration
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration
import org.jetbrains.dukat.tsmodel.types.StringLiteralDeclaration
import org.jetbrains.dukat.tsmodel.types.TypeDeclaration
import org.jetbrains.dukat.tsmodel.types.TypeParamReferenceDeclaration
import org.jetbrains.dukat.tsmodel.types.UnionTypeDeclaration

private fun ParameterValueDeclaration.makeNullable(): ParameterValueDeclaration {
    if (nullable) {
        return this
    }

    return when (this) {
        is NumericLiteralDeclaration -> copy(nullable = true)
        is IntersectionTypeDeclaration -> copy(nullable = true)
        is StringLiteralDeclaration -> copy(nullable = true)
        is TypeDeclaration -> copy(nullable = true)
        is TypeParamReferenceDeclaration -> copy(nullable = true)
        is FunctionTypeDeclaration -> copy(nullable = true)
        is UnionTypeDeclaration -> copy(params = params.map { it.makeNullable() }, nullable = true)
        is GeneratedInterfaceReferenceDeclaration -> copy(nullable = true)
        is ObjectLiteralDeclaration -> copy(nullable = true)
        else -> raiseConcern("makeNullable does not recognize type ${this}") { this }
    }
}

private fun UnionTypeDeclaration.simplifyUnionDeclaration(): ParameterValueDeclaration {
    var canBeNull = false
    val paramsResolved = params.mapNotNull {
        if (it.nullable) {
            canBeNull = true
        }
        if (it is TypeDeclaration) {
            when (it.value) {
                IdentifierEntity("undefined") -> {
                    canBeNull = true
                    null
                }
                IdentifierEntity("null") -> {
                    canBeNull = true
                    null
                }
                else -> it
            }
        } else it
    }

    val paramsNullified = paramsResolved.map { if (canBeNull) it.makeNullable() else it }

    return if (paramsNullified.isEmpty()) {
        TypeDeclaration(IdentifierEntity("Nothing"), emptyList(), null, true)
    } else if (paramsNullified.size == 1) {
        paramsNullified[0]
    } else {
        copy(params = paramsNullified)
    }
}

private class PreprocessUnionTypesLowering : DeclarationLowering {
    override fun lowerUnionTypeDeclaration(declaration: UnionTypeDeclaration, owner: NodeOwner<ParameterOwnerDeclaration>?): ParameterValueDeclaration {
        val declarationSimplified = declaration.simplifyUnionDeclaration()
        return if (declarationSimplified is UnionTypeDeclaration) {
            super.lowerUnionTypeDeclaration(declarationSimplified, owner)
        } else {
            super.lowerParameterValue(declarationSimplified, owner)
        }
    }
}

private class LowerObjectType : DeclarationLowering {

    override fun lowerTypeDeclaration(declaration: TypeDeclaration, owner: NodeOwner<ParameterOwnerDeclaration>?): TypeDeclaration {
        val declarationResolved = if (declaration.value == IdentifierEntity("object")) {
            TypeDeclaration(IdentifierEntity("Any"), emptyList(), null, true)
        } else {
            declaration
        }
        return super.lowerTypeDeclaration(declarationResolved, owner)
    }
}


class PreprocessUnionTypes : TsLowering {
    override fun lower(source: SourceSetDeclaration): SourceSetDeclaration {
        return source.copy(sources = source.sources.map { sourceFile ->
            sourceFile.copy(
                    root = LowerObjectType().lowerSourceDeclaration(sourceFile.root).let {
                        PreprocessUnionTypesLowering().lowerSourceDeclaration(it)
                    }
            )
        })
    }
}