package org.jetbrains.dukat.commonLowerings

import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.astModel.ModuleModel
import org.jetbrains.dukat.astModel.SourceFileModel
import org.jetbrains.dukat.astModel.SourceSetModel

fun SourceSetModel.splitIntoSeparateEntities(): SourceSetModel {
    return copy(sources = sources.flatMap { source ->
        source.root.declarations.map { declaration ->
            SourceFileModel(
                    name = declaration.name,
                    fileName = source.fileName,
                    root = ModuleModel(
                            name = IdentifierEntity("<LIBROOT>"),
                            shortName = IdentifierEntity("<LIBROOT>"),
                            declarations = listOf(declaration),
                            annotations = mutableListOf(),
                            submodules = emptyList(),
                            imports = source.root.imports,
                            comment = null
                    ),
                    referencedFiles = emptyList()
            )
        }
    })
}