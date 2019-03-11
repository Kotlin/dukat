package org.jetbrains.dukat.ast.model.model

data class SourceSetModel(val sources: List<SourceFileModel>)

fun SourceSetModel.transform(rootHandler: (ModuleModel) -> ModuleModel)
            = copy(sources = sources.map { source -> source.transform(rootHandler) })
