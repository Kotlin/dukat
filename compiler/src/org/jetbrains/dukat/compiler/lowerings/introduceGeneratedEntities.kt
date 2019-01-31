package org.jetbrains.dukat.compiler.lowerings

import org.jetbrains.dukat.ast.model.nodes.ClassLikeNode
import org.jetbrains.dukat.compiler.AstContext
import org.jetbrains.dukat.tsmodel.DocumentRootDeclaration

fun DocumentRootDeclaration.introduceGeneratedEntities(astContext: AstContext): DocumentRootDeclaration {

    val declarations = this.declarations.map { declaration ->
        if (declaration is ClassLikeNode) {
            astContext.resolveGeneratedInterfacesFor(declaration) + listOf(declaration)
        } else {
            listOf(declaration)
        }
    }.flatten()

    return copy(declarations = declarations)
}