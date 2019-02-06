package org.jetbrains.dukat.ast.model.model

import org.jetbrains.dukat.ast.model.nodes.AnnotationNode
import org.jetbrains.dukat.astCommon.TopLevelDeclaration

data class ModuleModel(
        val packageName: String,
        val declarations: List<TopLevelDeclaration> = emptyList(),
        val annotations: MutableList<AnnotationNode>,

        val sumbodules: List<ModuleModel>
) : TopLevelDeclaration