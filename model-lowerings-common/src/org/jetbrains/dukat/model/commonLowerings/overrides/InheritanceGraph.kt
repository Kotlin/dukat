package org.jetbrains.dukat.model.commonLowerings.overrides

import org.jetbrains.dukat.astModel.ClassLikeModel
import org.jetbrains.dukat.graphs.Graph
import org.jetbrains.dukat.graphs.Vertex
import org.jetbrains.dukat.model.commonLowerings.ModelContext

class InheritanceVertex(val classLikeModel: ClassLikeModel, private val context: ModelContext): Vertex<ClassLikeModel, InheritanceVertex> {
    override val adjacents: Iterable<InheritanceVertex>
        get() = context.getAllParents(classLikeModel).map { InheritanceVertex(it.classLike, context) }
}

class InheritanceGraph(private val context: ModelContext) : Graph<InheritanceVertex> {
    private val verticeMap: MutableMap<ClassLikeModel, Set<ClassLikeModel>> = mutableMapOf()
    private val verticesList = context.getClassLikeIterable().map { classLikeModel ->
        val vertex = InheritanceVertex(classLikeModel, context)
        verticeMap[classLikeModel] = vertex.adjacents.map { it.classLikeModel }.toSet()
        vertex
    }

    fun getAdjacentVertices(classLikeModel: ClassLikeModel?): Set<ClassLikeModel>? {
        return verticeMap[classLikeModel]
    }

    override val vertices = verticesList
}

