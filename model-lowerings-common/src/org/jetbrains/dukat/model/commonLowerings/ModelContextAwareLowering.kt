package org.jetbrains.dukat.model.commonLowerings

import org.jetbrains.dukat.astModel.ClassLikeModel
import org.jetbrains.dukat.astModel.ModuleModel
import org.jetbrains.dukat.astModel.SourceSetModel
import org.jetbrains.dukat.toposort.Graph
import org.jetbrains.dukat.model.commonLowerings.overrides.InheritanceContext

class ModelContextAwareLowering(private val translationContext: TranslationContext) : ModelLowering {
    override fun lower(module: ModuleModel): ModuleModel = module

    override fun lower(source: SourceSetModel): SourceSetModel {
        translationContext.initModelContext(ModelContext(source))
        translationContext.initInheritanceContext(InheritanceContext(translationContext.modelContext.buildInheritanceGraph()))
        return super.lower(source)
    }

    private fun ModelContext.buildInheritanceGraph(): Graph<ClassLikeModel> {
        val graph = Graph<ClassLikeModel>()

        getClassLikeIterable().forEach { classLike ->
            getAllParents(classLike).forEach { resolvedClassLike ->
                graph.addEdge(classLike, resolvedClassLike.classLike)
            }
        }

        return graph
    }
}