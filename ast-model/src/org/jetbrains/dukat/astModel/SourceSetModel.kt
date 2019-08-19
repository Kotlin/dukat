package org.jetbrains.dukat.astModel

data class SourceSetModel(val sourceName: String, val sources: List<SourceFileModel>)

fun SourceSetModel.transform(rootHandler: (ModuleModel) -> ModuleModel)
            = copy(sources = sources.map { source -> source.transform(rootHandler) })