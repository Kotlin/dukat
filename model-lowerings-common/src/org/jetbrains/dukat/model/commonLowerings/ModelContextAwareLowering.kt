package org.jetbrains.dukat.model.commonLowerings

import org.jetbrains.dukat.astModel.ClassLikeModel
import org.jetbrains.dukat.astModel.SourceSetModel
import org.jetbrains.dukat.graphs.Graph
import org.jetbrains.dukat.model.commonLowerings.overrides.InheritanceContext

internal fun ModelContext.buildInheritanceGraph(): Graph<ClassLikeModel> {
    val graph = Graph<ClassLikeModel>()

    getClassLikeIterable().forEach { classLike ->
        getAllParents(classLike).forEach { resolvedClassLike ->
            graph.addEdge(classLike, resolvedClassLike.classLike)
        }
    }

    return graph
}

abstract class ModuleModelContextAwareLowering(
        internal val modelContext: ModelContext,
        internal val inheritanceContext: InheritanceContext
) : ModuleModelLowering {}

class ModelContextAwareLowering : ComposableModelLowering {
    internal lateinit var modelContext: ModelContext
    internal lateinit var inheritanceContext: InheritanceContext
    private val factories = mutableListOf<(ModelContext, InheritanceContext) -> ModuleModelLowering>()

    override val lowerings: List<ModuleModelLowering>
        get() = factories.map { factory -> factory(modelContext, inheritanceContext) }


    fun lower(factory: (ModelContext, InheritanceContext) -> ModuleModelLowering): ModelContextAwareLowering {
        factories.add(factory)
        return this
    }

    override fun lower(source: SourceSetModel): SourceSetModel {
        modelContext = ModelContext(source)
        inheritanceContext = InheritanceContext(modelContext.buildInheritanceGraph())
        return super.lower(source)
    }
}