package org.jetbrains.dukat.compiler.lowerings

import org.jetbrains.dukat.tsmodel.DocumentRootDeclaration
import org.jetbrains.dukat.tsmodel.types.TypeDeclaration

private class LowerOptional : ParameterValueLowering {

    override fun lowerTypeDeclaration(declaration: TypeDeclaration): TypeDeclaration {
        return super.lowerTypeDeclaration(declaration)
    }


}

fun DocumentRootDeclaration.lowerOptional(): DocumentRootDeclaration {
 //   return LowerDeclarationsToNodes().lowerDocumentRoot(this)
    return this
}