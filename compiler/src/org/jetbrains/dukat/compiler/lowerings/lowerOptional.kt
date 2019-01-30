package org.jetbrains.dukat.compiler.lowerings

import org.jetbrains.dukat.ast.model.declaration.DocumentRootDeclaration
import org.jetbrains.dukat.ast.model.declaration.types.TypeDeclaration

private class LowerOptional : ParameterValueLowering {

    override fun lowerTypeDeclaration(declaration: TypeDeclaration): TypeDeclaration {
        return super.lowerTypeDeclaration(declaration)
    }


}

fun DocumentRootDeclaration.lowerOptional(): DocumentRootDeclaration {
 //   return LowerDeclarationsToNodes().lowerDocumentRoot(this)
    return this
}