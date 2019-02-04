package org.jetbrains.dukat.compiler.lowerings

import org.jetbrains.dukat.ast.model.nodes.AnnotationNode
import org.jetbrains.dukat.ast.model.nodes.DocumentRootNode
import org.jetbrains.dukat.ast.model.nodes.FunctionNode
import org.jetbrains.dukat.ast.model.nodes.VariableNode
import org.jetbrains.dukat.tsmodel.ExportAssignmentDeclaration


fun DocumentRootNode.introduceDefaultExports(): DocumentRootNode {
    declarations.map {
        when (it) {
            is DocumentRootNode -> it.introduceDefaultExports()

            is ExportAssignmentDeclaration -> {
                val defaultAnnotation = AnnotationNode("JsName", listOf("default"))

                val functions = declarations.filterIsInstance(FunctionNode::class.java)
                val function = functions.find { f -> f.name == it.name }

                if (function != null) {
                    function.annotations.add(defaultAnnotation)
                } else {
                    val vars = declarations.filterIsInstance(VariableNode::class.java)
                    val variable = vars.find { v -> v.name == it.name}

                    if (variable != null) {
                        variable.annotations.add(defaultAnnotation)
                    }
                }

                it
            }

            else -> it
        }
    }

    return this
}