package org.jetbrains.dukat.tsLowerings

import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.ownerContext.NodeOwner
import org.jetbrains.dukat.tsmodel.ClassLikeDeclaration
import org.jetbrains.dukat.tsmodel.HeritageClauseDeclaration
import org.jetbrains.dukat.tsmodel.ModuleDeclaration
import org.jetbrains.dukat.tsmodel.ParameterOwnerDeclaration
import org.jetbrains.dukat.tsmodel.SourceSetDeclaration
import org.jetbrains.dukat.tsmodel.types.TypeDeclaration

private class SyncNameLowering(private val renameMap: Map<String, NameEntity>) : DeclarationTypeLowering {

    override fun lowerHeritageClause(heritageClause: HeritageClauseDeclaration, owner: NodeOwner<ClassLikeDeclaration>?): HeritageClauseDeclaration {
        val heritageClauseLowered = renameMap[heritageClause.typeReference?.uid]?.let {
            heritageClause.copy(name = it)
        } ?: heritageClause
        return super.lowerHeritageClause(heritageClauseLowered, owner)
    }

    override fun lowerTypeDeclaration(declaration: TypeDeclaration, owner: NodeOwner<ParameterOwnerDeclaration>?): TypeDeclaration {
        val declarationLowered = renameMap[declaration.typeReference?.uid]?.let {
            declaration.copy(value = it)
        } ?: declaration
        return super.lowerTypeDeclaration(declarationLowered, owner)
    }
}

private fun ModuleDeclaration.syncTypeNames(renameMap: Map<String, NameEntity>): ModuleDeclaration {
    return SyncNameLowering(renameMap).lowerDocumentRoot(this)
}

fun SourceSetDeclaration.syncTypeNames(renameMap: Map<String, NameEntity>): SourceSetDeclaration {
    return copy(sources = sources.map { source -> source.copy(root = source.root.syncTypeNames(renameMap)) })
}