package org.jetbrains.dukat.compiler.lowerings

import org.jetbrains.dukat.ast.model.model.ModuleModel
import org.jetbrains.dukat.ast.model.nodes.AnnotationNode
import org.jetbrains.dukat.ast.model.nodes.DocumentRootNode
import org.jetbrains.dukat.astCommon.TopLevelDeclaration


fun DocumentRootNode.introduceRepresentationModels(): ModuleModel {
    val declarations = declarations.map {declaration ->
        when (declaration) {
            is DocumentRootNode -> declaration.introduceRepresentationModels()
            else -> declaration
        }
    }


    val declarationsFiltered = mutableListOf<TopLevelDeclaration>()
    val submodules = mutableListOf<ModuleModel>()
    declarations.forEach { declaration ->
        if (declaration is ModuleModel) submodules.add(declaration) else declarationsFiltered.add(declaration)
    }

    val annotations = mutableListOf<AnnotationNode>()

    if (showQualifierAnnotation) {
        val qualifier = if (isQualifier) "JsQualifier" else "JsModule"
        annotations.add(AnnotationNode("file:${qualifier}", listOf(qualifierName)))
    }

    return ModuleModel(
        packageName = fullPackageName,
        declarations = declarationsFiltered,
        annotations = annotations,
        sumbodules = submodules
    )
}