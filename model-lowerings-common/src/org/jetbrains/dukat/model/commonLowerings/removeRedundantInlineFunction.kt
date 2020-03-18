package org.jetbrains.dukat.model.commonLowerings

import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astModel.ClassLikeReferenceModel
import org.jetbrains.dukat.astModel.FunctionModel
import org.jetbrains.dukat.astModel.ModuleModel
import org.jetbrains.dukat.astModel.SourceSetModel
import org.jetbrains.dukat.astModel.TypeModel

private data class FunctionModelRecord(
        val name: NameEntity,
        val reference: ClassLikeReferenceModel?,
        val params: List<TypeModel>,
        val type: TypeModel
)

private fun ModuleModel.collectInlineFunctions(handler: (FunctionModel) -> Unit) {
    declarations.filterIsInstance(FunctionModel::class.java).filter { it.extend != null }.forEach(handler)
    submodules.forEach { it.collectInlineFunctions(handler) }
}

private enum class GenerationState {
    IS_ALREADY_ADDED,
    IS_ABSENT
}

private fun FunctionModel.convertToRecord(): FunctionModelRecord {
    return FunctionModelRecord(
            name = name,
            reference = extend,
            params = parameters.map { it.type },
            type = type
    )
}

private fun ModuleModel.removeRedundantInlines(generatedFunctions: MutableMap<FunctionModelRecord, GenerationState>): ModuleModel {
    val declarationsResolved = declarations.mapNotNull {
        when (it) {
            is FunctionModel -> {
                val key = it.convertToRecord()
                when (generatedFunctions[key]) {
                    GenerationState.IS_ABSENT -> {
                        generatedFunctions[key] = GenerationState.IS_ALREADY_ADDED
                        it
                    }
                    GenerationState.IS_ALREADY_ADDED -> null
                    else -> it
                }
            }
            else -> it
        }
    }

    return copy(declarations = declarationsResolved, submodules = submodules.map { it.removeRedundantInlines(generatedFunctions) })
}

private fun SourceSetModel.removeRedundantInlineFunction(): SourceSetModel {

    val generatedFunctions = mutableMapOf<FunctionModelRecord, GenerationState>()

    sources.forEach { source ->
        source.root.collectInlineFunctions { functionModel ->
            generatedFunctions.putIfAbsent(functionModel.convertToRecord(), GenerationState.IS_ABSENT)
        }
    }

    return copy(sources = sources.map { it.copy(root = it.root.removeRedundantInlines(generatedFunctions)) })
}

class RemoveRedundantInlineFunction() : ModelLowering {
    override fun lower(source: SourceSetModel): SourceSetModel {
        return source.removeRedundantInlineFunction()
    }
}