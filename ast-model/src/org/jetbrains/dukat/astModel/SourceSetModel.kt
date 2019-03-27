package org.jetbrains.dukat.astModel

data class SourceSetModel(val sources: List<SourceFileModel>)

fun SourceSetModel.transform(rootHandler: (ModuleModel) -> ModuleModel)
            = copy(sources = sources.map { source -> source.transform(rootHandler) })


fun SourceSetModel.getSourceFile(fileName: String): SourceFileModel? {
    return sources.firstOrNull {
        it.fileName == fileName
    }
}
