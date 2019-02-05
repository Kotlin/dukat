package org.jetbrains.dukat.compiler.lowerings

import org.jetbrains.dukat.ast.model.nodes.AnnotationNode
import org.jetbrains.dukat.ast.model.nodes.DocumentRootNode
import org.jetbrains.dukat.ast.model.nodes.FunctionNode
import org.jetbrains.dukat.ast.model.nodes.VariableNode
import org.jetbrains.dukat.tsmodel.ExportAssignmentDeclaration


fun DocumentRootNode.introduceDefaultExports(): DocumentRootNode {
    declarations.map { declaration ->
        when (declaration) {
            is DocumentRootNode -> declaration.introduceDefaultExports()

            is ExportAssignmentDeclaration -> {
                val defaultAnnotation = AnnotationNode("JsName", listOf("default"))

                val functions = declarations.filterIsInstance(FunctionNode::class.java)
                val function = functions.find { f -> f.uid == declaration.name }

                if (function != null) {
                    function.annotations.add(defaultAnnotation)
                } else {
                    val vars = declarations.filterIsInstance(VariableNode::class.java)
                    val variable = vars.find { v -> v.uid == declaration.name}

                    if (variable != null) {
                        variable.annotations.add(defaultAnnotation)
                    }
                }

                declaration
            }

            else -> declaration
        }
    }

    return this
}