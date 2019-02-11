package org.jetbrains.dukat.tsmodel.lowerings

import org.jetbrains.dukat.tsmodel.ClassLikeDeclaration
import org.jetbrains.dukat.tsmodel.DocumentRootDeclaration
import org.jetbrains.dukat.tsmodel.FunctionDeclaration


fun DocumentRootDeclaration.introduceGeneratedEntities(tsAstContext: TsAstContext): DocumentRootDeclaration {

    val declarations = this.declarations.map { declaration ->
        if (declaration is ClassLikeDeclaration) {
            tsAstContext.resolveGeneratedInterfacesFor(declaration) + listOf(declaration)
        } else if (declaration is FunctionDeclaration) {
            tsAstContext.resolveGeneratedInterfacesFor(declaration) + listOf(declaration)
        } else if (declaration is DocumentRootDeclaration) {
            listOf(declaration.introduceGeneratedEntities(tsAstContext))
        } else {
            listOf(declaration)
        }
    }.flatten()

    return copy(declarations = declarations)
}