package org.jetbrains.dukat.model.commonLowerings

import org.jetbrains.dukat.astModel.ClassLikeModel
import org.jetbrains.dukat.astModel.SourceSetModel
import org.jetbrains.dukat.toposort.Graph
import org.jetbrains.dukat.model.commonLowerings.overrides.InheritanceContext

private fun ModelContext.buildInheritanceGraph(): Graph<ClassLikeModel> {
    val graph = Graph<ClassLikeModel>()

    getClassLikeIterable().forEach { classLike ->
        getAllParents(classLike).forEach { resolvedClassLike ->
            graph.addEdge(classLike, resolvedClassLike.classLike)
        }
    }

    return graph
}

class ModelContextAwareLowering : ComposableModelLowering {
    internal lateinit var modelContext: ModelContext
    internal lateinit var inheritanceContext: InheritanceContext
    private val factories = mutableListOf<(ModelContext, InheritanceContext) -> ModelLowering>()

    override val lowerings: List<ModelLowering>
        get() = factories.map { factory -> factory(modelContext, inheritanceContext) }


    fun lower(factory: (ModelContext, InheritanceContext) -> ModelLowering): ModelContextAwareLowering {
        factories.add(factory)
        return this
    }

    override fun lower(source: SourceSetModel): SourceSetModel {
        modelContext = ModelContext(source)
        inheritanceContext = InheritanceContext(modelContext.buildInheritanceGraph())
        return super.lower(source)
    }
}