package org.jetbrains.dukat.compiler.lowerings

import org.jetbrains.dukat.ast.model.model.ModuleModel
import org.jetbrains.dukat.ast.model.nodes.AnnotationNode
import org.jetbrains.dukat.ast.model.nodes.DocumentRootNode


fun DocumentRootNode.introduceRepresentationModels(): ModuleModel {
    val  declarations = declarations.map {declaration ->
        when (declaration) {
            is DocumentRootNode -> declaration.introduceRepresentationModels()
            else -> declaration
        }
    }

    val annotations = mutableListOf<AnnotationNode>()

    if (showQualifierAnnotation) {
        val qualifier = if (isQualifier) "JsQualifier" else "JsModule"
        annotations.add(AnnotationNode("file:${qualifier}", listOf(qualifierName)))
    }

    return ModuleModel(
        packageName = fullPackageName,
        declarations = declarations,
        annotations = annotations
    )
}