package org.jetbrains.dukat.tsmodel.lowerings

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

private class PartialLowering : DeclarationTypeLowering {
    override fun lowerParameterValue(declaration: ParameterValueDeclaration): ParameterValueDeclaration {
        return when (declaration) {
            is TypeDeclaration -> if (((declaration.value is IdentifierEntity)) && (declaration.value.value == "Partial")) {
                if ((declaration.params.size == 1) && (declaration.params[0] is ObjectLiteralDeclaration)) {
                    val objectLiteral = (declaration.params.get(0) as ObjectLiteralDeclaration)
                    super.lowerParameterValue(objectLiteral.copy(members = objectLiteral.members.map { member ->
                        member.makeOptional()
                    }))
                } else {
                    super.lowerParameterValue(declaration)
                }
            } else {
                super.lowerParameterValue(declaration)
            }
            else -> super.lowerParameterValue(declaration)
        }
    }
}

private fun ModuleDeclaration.resolvePartials(): ModuleDeclaration {
    return PartialLowering().lowerDocumentRoot(this)
}

private fun SourceFileDeclaration.resolvePartials() = copy(root = root.resolvePartials())

fun SourceSetDeclaration.resolvePartials() = copy(sources = sources.map(SourceFileDeclaration::resolvePartials))