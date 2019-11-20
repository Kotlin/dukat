package org.jetbrains.dukat.tsLowerings

import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.tsLowerings.stdlib.stdLibRenameMap
import org.jetbrains.dukat.tsmodel.InterfaceDeclaration
import org.jetbrains.dukat.tsmodel.ModuleDeclaration
import org.jetbrains.dukat.tsmodel.SourceSetDeclaration

private fun ModuleDeclaration.renameStdLibEntities(onRename: (uid: String, newName: NameEntity) -> Unit): ModuleDeclaration {
    val declarationsResolved = declarations.map { declaration ->
        when (declaration) {
            is InterfaceDeclaration -> stdLibRenameMap.resolve(declaration.name)?.let {
                onRename(declaration.uid, it)
                declaration.copy(name = it)
            } ?: declaration
            is ModuleDeclaration -> declaration.renameStdLibEntities(onRename)
            else -> declaration
        }
    }

    return copy(declarations = declarationsResolved)
}

fun SourceSetDeclaration.renameStdLibEntities(onRename: (uid: String, newName: NameEntity) -> Unit): SourceSetDeclaration {
    return copy(sources = sources.map { source -> source.copy(root = source.root.renameStdLibEntities(onRename)) })
}