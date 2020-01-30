package org.jetbrains.dukat.tsLowerings

import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.tsmodel.ModuleDeclaration
import org.jetbrains.dukat.tsmodel.SourceFileDeclaration
import org.jetbrains.dukat.tsmodel.SourceSetDeclaration

private fun ModuleDeclaration.addPackageName(packageName: NameEntity): ModuleDeclaration {
    return copy(packageName = packageName, declarations = declarations.map {
        when (it) {
            is ModuleDeclaration -> it.addPackageName(packageName)
            else -> it
        }
    })
}

private fun SourceFileDeclaration.addPackageName(packageName: NameEntity): SourceFileDeclaration {
    return copy(root = root.addPackageName(packageName))
}

fun SourceSetDeclaration.addPackageName(packageName: NameEntity?): SourceSetDeclaration {
    return packageName?.let { packageNameResolved ->
        copy(sources = sources.map { it.addPackageName(packageNameResolved) })
    } ?: this
}