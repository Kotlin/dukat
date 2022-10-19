package org.jetbrains.dukat.tsLowerings

import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.ownerContext.NodeOwner
import org.jetbrains.dukat.tsmodel.*
import org.jetbrains.dukat.tsmodel.types.ObjectLiteralDeclaration
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration
import org.jetbrains.dukat.tsmodel.types.TypeDeclaration
import org.jetbrains.dukat.tsmodel.types.TypeParamReferenceDeclaration


private fun MemberDeclaration.makeOptional(): MemberDeclaration {
    return when (this) {
        is PropertyDeclaration -> copy(optional = true)
        else -> this
    }
}

private fun TypeDeclaration.resolvePartial(): ParameterValueDeclaration? {
    val value = value

    if (value !is IdentifierEntity || value.value != "Partial") return null

    val objectLiteral = params.singleOrNull() as? ObjectLiteralDeclaration ?: return null

    return objectLiteral.copy(members = objectLiteral.members.map { member ->
        member.makeOptional()
    })
}

private fun HeritageClauseDeclaration.resolvePartial(): HeritageClauseDeclaration? {
    val value = name

    if (value !is IdentifierEntity || value.value != "Partial") return null

    val typeParameter = typeArguments.singleOrNull() as? TypeDeclaration ?: return HeritageClauseDeclaration(
        IdentifierEntity("Any"),
        emptyList(),
        extending,
        null
    )

    return copy(
        name = typeParameter.value,
        typeArguments = typeParameter.params,
        extending,
        typeReference = typeParameter.typeReference
    )
}

private fun HeritageClauseDeclaration.resolvePick(): HeritageClauseDeclaration? {
    val value = name

    if (value !is IdentifierEntity || value.value != "Pick") return null

    val typeParameter = typeArguments.firstOrNull().takeIf { typeArguments.size == 2 } as? TypeDeclaration
        ?: return HeritageClauseDeclaration(IdentifierEntity("Any"), emptyList(), extending, null)

    return copy(
        name = typeParameter.value,
        typeArguments = typeParameter.params,
        extending,
        typeReference = typeParameter.typeReference
    )
}

private fun TypeDeclaration.resolvePick(): ParameterValueDeclaration? {
    val value = value

    if (value !is IdentifierEntity || value.value != "Pick") return null
    if (params.size != 2 || params[0] !is TypeParamReferenceDeclaration) return null

    return TypeDeclaration(IdentifierEntity("Any"), emptyList())
}

private class UtilityTypeLowering : DeclarationLowering {
    override fun lowerParameterValue(declaration: ParameterValueDeclaration, owner: NodeOwner<ParameterOwnerDeclaration>?): ParameterValueDeclaration {
        val declarationLowered = when (declaration) {
            is TypeDeclaration -> declaration.resolvePartial()
                    ?: declaration.resolvePick()
                    ?: declaration
            else -> declaration
        }

        return super.lowerParameterValue(declarationLowered, owner)
    }

    override fun lowerHeritageClause(
        heritageClause: HeritageClauseDeclaration,
        owner: NodeOwner<ClassLikeDeclaration>?
    ): HeritageClauseDeclaration {
        val heritage = heritageClause.resolvePartial() ?: heritageClause.resolvePick() ?: heritageClause
        return super.lowerHeritageClause(heritage, owner)
    }
}

private fun ModuleDeclaration.resolveTypescriptUtilityTypes(): ModuleDeclaration {
    return UtilityTypeLowering().lowerSourceDeclaration(this)
}

private fun SourceFileDeclaration.resolveTypescriptUtilityTypes() = copy(root = root.resolveTypescriptUtilityTypes())

private fun SourceSetDeclaration.resolveTypescriptUtilityTypes() = copy(sources = sources.map(SourceFileDeclaration::resolveTypescriptUtilityTypes))

class ResolveTypescriptUtilityTypes() : TsLowering {
    override fun lower(source: SourceSetDeclaration): SourceSetDeclaration {
        return source.resolveTypescriptUtilityTypes()
    }
}