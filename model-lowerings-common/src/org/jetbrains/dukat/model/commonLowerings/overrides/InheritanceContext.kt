package org.jetbrains.dukat.model.commonLowerings.overrides

import org.jetbrains.dukat.astModel.ClassLikeModel

class InheritanceContext(private val inheritanceGraph: InheritanceGraph) {
    fun isDescendant(classLikeModel: ClassLikeModel?, parentCandidate: ClassLikeModel?): Boolean {
        return inheritanceGraph.getAdjacentVertices(classLikeModel)?.contains(parentCandidate) == true
    }

    fun areRelated(classLikeModelA: ClassLikeModel?, classLikeModelB: ClassLikeModel?): Boolean {
        return (classLikeModelA == classLikeModelB) || (isDescendant(classLikeModelA, classLikeModelB) || isDescendant(classLikeModelB, classLikeModelA))
    }
}