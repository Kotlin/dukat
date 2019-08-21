package org.jetbrains.dukat.idlLowerings

import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astCommon.QualifierEntity
import org.jetbrains.dukat.astCommon.toNameEntity
import org.jetbrains.dukat.astModel.ModuleModel
import org.jetbrains.dukat.astModel.SourceFileModel
import org.jetbrains.dukat.astModel.SourceSetModel

private data class Relocation(val name: NameEntity, val from: NameEntity, val to: NameEntity)

private val relocations: Set<Relocation> = setOf(
        Relocation("Event".toNameEntity(), "org.w3c.dom".toNameEntity(), "org.w3c.dom.events".toNameEntity()),
        Relocation("EventTarget".toNameEntity(), "org.w3c.dom".toNameEntity(), "org.w3c.dom.events".toNameEntity()),
        Relocation("EventListener".toNameEntity(), "org.w3c.dom".toNameEntity(), "org.w3c.dom.events".toNameEntity())
)

fun SourceSetModel.relocateDeclarations(): SourceSetModel {
    val newSources = sources.toMutableList()
    for (relocation in relocations) {
        val fileFrom = newSources.find { it.root.name == relocation.from }
        val fileTo = newSources.find { it.root.name == relocation.to }
        val declarationToMove = fileFrom?.root?.declarations?.find { it.name == relocation.name }
        if (fileTo != null && fileFrom != null && declarationToMove != null) {
            newSources.replaceAll {
                it.copy(root = it.root.copy(declarations = when (it) {
                    fileFrom -> it.root.declarations - declarationToMove
                    fileTo -> it.root.declarations + declarationToMove
                    else -> it.root.declarations
                }))
            }
        }
    }
    return copy(sources = newSources)
}