package org.jetbrains.dukat.compiler.lowerings

import org.jetbrains.dukat.ast.model.nodes.ClassLikeNode
import org.jetbrains.dukat.ast.model.nodes.DocumentRootNode
import org.jetbrains.dukat.ast.model.nodes.FunctionNode
import org.jetbrains.dukat.compiler.AstContext

fun DocumentRootNode.introduceGeneratedEntities(astContext: AstContext): DocumentRootNode {

    val declarations = this.declarations.map { declaration ->
        if (declaration is ClassLikeNode) {
            astContext.resolveGeneratedInterfacesFor(declaration) + listOf(declaration)
        } else if (declaration is FunctionNode) {
            astContext.resolveGeneratedInterfacesFor(declaration) + listOf(declaration)
        } else if (declaration is DocumentRootNode) {
            listOf(declaration.introduceGeneratedEntities(astContext))
        } else {
            listOf(declaration)
        }
    }.flatten()

    return copy(declarations = declarations)
}