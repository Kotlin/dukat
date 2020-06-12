package org.jetbrains.dukat.model.commonLowerings.overrides

import org.jetbrains.dukat.astModel.ClassLikeModel
import org.jetbrains.dukat.graphs.Graph

class InheritanceContext(private val inheritanceGraph: Graph<ClassLikeModel>) {
    fun isDescendant(classLikeModelA: ClassLikeModel?, classLikeModelB: ClassLikeModel?): Boolean {
        return inheritanceGraph.areConnected(classLikeModelA, classLikeModelB)
    }

    fun areRelated(classLikeModelA: ClassLikeModel?, classLikeModelB: ClassLikeModel?): Boolean {
        return (classLikeModelA == classLikeModelB) || (isDescendant(classLikeModelA, classLikeModelB) || isDescendant(classLikeModelB, classLikeModelA))
    }
}