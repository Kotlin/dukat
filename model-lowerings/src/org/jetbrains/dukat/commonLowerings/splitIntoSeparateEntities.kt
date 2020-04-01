package org.jetbrains.dukat.commonLowerings

import org.jetbrains.dukat.astModel.ModuleModel
import org.jetbrains.dukat.astModel.SourceFileModel
import org.jetbrains.dukat.astModel.SourceSetModel
import org.jetbrains.dukat.stdlib.TSLIBROOT

fun SourceSetModel.splitIntoSeparateEntities(): SourceSetModel {
    return copy(sources = sources.flatMap { source ->
        source.root.declarations.map { declaration ->
            SourceFileModel(
                    name = declaration.name,
                    fileName = source.fileName,
                    root = ModuleModel(
                            name = TSLIBROOT,
                            shortName = TSLIBROOT,
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