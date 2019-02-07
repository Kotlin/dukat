package org.jetbrains.dukat.compiler.lowerings

import org.jetbrains.dukat.ast.model.nodes.DocumentRootNode

abstract class ModuleAwareTypeLowering(private val moduleNode: DocumentRootNode) : ParameterValueLowering {

    fun lower(): DocumentRootNode {
        return moduleNode.copy(declarations = lowerTopLevelDeclarations(moduleNode.declarations))
    }

    abstract override fun lowerDocumentRoot(documentRoot: DocumentRootNode): DocumentRootNode
}