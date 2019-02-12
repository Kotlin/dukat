package org.jetbrains.dukat.tsmodel.lowerings

import org.jetbrains.dukat.astCommon.TopLevelDeclaration
import org.jetbrains.dukat.tsmodel.ClassLikeDeclaration
import org.jetbrains.dukat.tsmodel.DocumentRootDeclaration
import org.jetbrains.dukat.tsmodel.FunctionDeclaration
import org.jetbrains.dukat.tsmodel.VariableDeclaration



private fun TopLevelDeclaration.introduceGeneratedEntities(astContext: TsAstContext): List<TopLevelDeclaration> {
    return when (this) {
        is ClassLikeDeclaration -> astContext.resolveGeneratedInterfacesFor(this).flatMap { genInterface -> genInterface.introduceGeneratedEntities(astContext) } + listOf(this)
        is VariableDeclaration -> astContext.resolveGeneratedInterfacesFor(this).flatMap { genInterface -> genInterface.introduceGeneratedEntities(astContext) } + listOf(this)
        is FunctionDeclaration -> astContext.resolveGeneratedInterfacesFor(this).flatMap { genInterface -> genInterface.introduceGeneratedEntities(astContext) } + listOf(this)
        is DocumentRootDeclaration -> listOf(this.introduceGeneratedEntities(astContext))
        else -> listOf(this)
    }
}


fun DocumentRootDeclaration.introduceGeneratedEntities(astContext: TsAstContext): DocumentRootDeclaration {

    val declarations = this.declarations.flatMap { declaration ->
        declaration.introduceGeneratedEntities(astContext)
    }

    return copy(declarations = declarations)
}