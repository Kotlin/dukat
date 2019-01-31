package org.jetbrains.dukat.compiler.lowerings

import org.jetbrains.dukat.tsmodel.DocumentRootDeclaration
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration
import org.jetbrains.dukat.tsmodel.types.StringTypeDeclaration
import org.jetbrains.dukat.tsmodel.types.TypeDeclaration


private class EliminateStringType : ParameterValueLowering {
    override fun lowerParameterValue(declaration: ParameterValueDeclaration): ParameterValueDeclaration {
        return if (declaration is StringTypeDeclaration) {
            TypeDeclaration("String", emptyList(), meta = declaration)
        } else declaration
    }
}

fun DocumentRootDeclaration.eliminateStringType(): DocumentRootDeclaration {
    return EliminateStringType().lowerDocumentRoot(this)
}