package org.jetbrains.dukat.tsmodel.lowerings

import org.jetbrains.dukat.tsmodel.ClassLikeDeclaration
import org.jetbrains.dukat.tsmodel.DocumentRootDeclaration
import org.jetbrains.dukat.tsmodel.FunctionDeclaration
import org.jetbrains.dukat.tsmodel.VariableDeclaration


fun DocumentRootDeclaration.introduceGeneratedEntities(tsAstContext: TsAstContext): DocumentRootDeclaration {

    val declarations = this.declarations.map { declaration ->
        when (declaration) {
            is ClassLikeDeclaration -> tsAstContext.resolveGeneratedInterfacesFor(declaration) + listOf(declaration)
            is VariableDeclaration -> tsAstContext.resolveGeneratedInterfacesFor(declaration) + listOf(declaration)
            is FunctionDeclaration -> tsAstContext.resolveGeneratedInterfacesFor(declaration) + listOf(declaration)
            is DocumentRootDeclaration -> listOf(declaration.introduceGeneratedEntities(tsAstContext))
            else -> listOf(declaration)
        }
    }.flatten()

    return copy(declarations = declarations)
}