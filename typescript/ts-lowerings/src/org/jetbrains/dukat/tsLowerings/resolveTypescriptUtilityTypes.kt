package org.jetbrains.dukat.tsLowerings

import org.jetbrains.dukat.astCommon.MemberEntity
import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.tsmodel.ModuleDeclaration
import org.jetbrains.dukat.tsmodel.PropertyDeclaration
import org.jetbrains.dukat.tsmodel.SourceFileDeclaration
import org.jetbrains.dukat.tsmodel.SourceSetDeclaration
import org.jetbrains.dukat.tsmodel.types.ObjectLiteralDeclaration
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration
import org.jetbrains.dukat.tsmodel.types.TypeDeclaration


private fun MemberEntity.makeOptional(): MemberEntity {
    return when (this) {
        is PropertyDeclaration -> copy(optional = true)
        else -> this
    }
}

private fun TypeDeclaration.resolvePartial(): ParameterValueDeclaration? {
    val value = value
    if (((value is IdentifierEntity)) && (value.value == "Partial")) {
        if ((params.size == 1) && (params[0] is ObjectLiteralDeclaration)) {
            val objectLiteral = (params[0] as ObjectLiteralDeclaration)
            return objectLiteral.copy(members = objectLiteral.members.map { member ->
                member.makeOptional()
            })
        }
    }

    return null
}

private fun TypeDeclaration.resolvePick(): ParameterValueDeclaration? {
    val value = value
    if (((value is IdentifierEntity)) && (value.value == "Pick")) {
        if (params.size == 2) {
            val tParam = params[0]
            if ((tParam is TypeDeclaration)) {
                if ((tParam.typeReference == null)) {
                    return TypeDeclaration(IdentifierEntity("Any"), emptyList())
                }
            }
        }
    }
    return null
}

private class UtilityTypeLowering : DeclarationTypeLowering {
    override fun lowerParameterValue(declaration: ParameterValueDeclaration): ParameterValueDeclaration {
        val declarationLowered = when (declaration) {
            is TypeDeclaration -> declaration.resolvePartial()
                                    ?: declaration.resolvePick()
                                    ?: declaration
            else -> declaration
        }

        return super.lowerParameterValue(declarationLowered)
    }
}

private fun ModuleDeclaration.resolveTypescriptUtilityTypes(): ModuleDeclaration {
    return UtilityTypeLowering().lowerDocumentRoot(this)
}

private fun SourceFileDeclaration.resolveTypescriptUtilityTypes() = copy(root = root.resolveTypescriptUtilityTypes())

fun SourceSetDeclaration.resolveTypescriptUtilityTypes() = copy(sources = sources.map(SourceFileDeclaration::resolveTypescriptUtilityTypes))