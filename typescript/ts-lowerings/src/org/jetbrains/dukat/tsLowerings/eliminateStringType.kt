package org.jetbrains.dukat.tsLowerings

import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.tsmodel.ModuleDeclaration
import org.jetbrains.dukat.tsmodel.SourceFileDeclaration
import org.jetbrains.dukat.tsmodel.SourceSetDeclaration
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration
import org.jetbrains.dukat.tsmodel.types.StringLiteralDeclaration
import org.jetbrains.dukat.tsmodel.types.TypeDeclaration


private class EliminateStringType : DeclarationTypeLowering {
    override fun lowerParameterValue(declaration: ParameterValueDeclaration): ParameterValueDeclaration {
        return if (declaration is StringLiteralDeclaration) {
            TypeDeclaration(IdentifierEntity("String"), emptyList(), meta = declaration)
        } else super.lowerParameterValue(declaration)
    }
}

fun ModuleDeclaration.eliminateStringType(): ModuleDeclaration {
    return EliminateStringType().lowerDocumentRoot(this)
}

fun SourceFileDeclaration.eliminateStringType() = copy(root = root.eliminateStringType())

fun SourceSetDeclaration.eliminateStringType() = copy(sources = sources.map(SourceFileDeclaration::eliminateStringType))