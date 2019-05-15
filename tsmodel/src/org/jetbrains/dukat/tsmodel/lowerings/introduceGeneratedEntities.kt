package org.jetbrains.dukat.tsmodel.lowerings

import org.jetbrains.dukat.astCommon.TopLevelEntity
import org.jetbrains.dukat.tsmodel.ClassLikeDeclaration
import org.jetbrains.dukat.tsmodel.FunctionDeclaration
import org.jetbrains.dukat.tsmodel.PackageDeclaration
import org.jetbrains.dukat.tsmodel.TypeAliasDeclaration
import org.jetbrains.dukat.tsmodel.VariableDeclaration


private fun TopLevelEntity.introduceGeneratedEntities(astContext: GeneratedInterfacesContext): List<TopLevelEntity> {
    return when (this) {
        is ClassLikeDeclaration -> astContext.resolveGeneratedInterfacesFor(this).flatMap { genInterface -> genInterface.introduceGeneratedEntities(astContext) } + listOf(this)
        is VariableDeclaration -> astContext.resolveGeneratedInterfacesFor(this).flatMap { genInterface -> genInterface.introduceGeneratedEntities(astContext) } + listOf(this)
        is FunctionDeclaration -> astContext.resolveGeneratedInterfacesFor(this).flatMap { genInterface -> genInterface.introduceGeneratedEntities(astContext) } + listOf(this)
        is TypeAliasDeclaration -> astContext.resolveGeneratedInterfacesFor(this).flatMap { genInterface -> genInterface.introduceGeneratedEntities(astContext) } + listOf(this)
        is PackageDeclaration -> listOf(this.introduceGeneratedEntities(astContext))
        else -> listOf(this)
    }
}


fun PackageDeclaration.introduceGeneratedEntities(astContext: GeneratedInterfacesContext): PackageDeclaration {

    val declarations = this.declarations.flatMap { declaration ->
        declaration.introduceGeneratedEntities(astContext)
    }

    return copy(declarations = declarations)
}