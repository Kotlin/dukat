package org.jetbrains.dukat.tsLowerings

import MergeableDeclaration
import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astCommon.QualifierEntity
import org.jetbrains.dukat.ownerContext.NodeOwner
import org.jetbrains.dukat.tsmodel.ClassLikeDeclaration
import org.jetbrains.dukat.tsmodel.ModuleDeclaration
import org.jetbrains.dukat.tsmodel.SourceSetDeclaration
import org.jetbrains.dukat.tsmodel.TopLevelDeclaration
import org.jetbrains.dukat.tsmodel.TypeAliasDeclaration


private fun TopLevelDeclaration.getName(): NameEntity? {
    return when (this) {
        is ClassLikeDeclaration -> name
        is ModuleDeclaration -> IdentifierEntity(name)
        else -> null
    }
}

private fun NameEntity.replaceRightMost(handler: (String) -> String): NameEntity {
    return when (this) {
        is IdentifierEntity -> copy(value = handler(value))
        is QualifierEntity -> copy(right = right.replaceRightMost(handler) as IdentifierEntity)
    }
}

private abstract class MoveIllegalAliasesLowering : DeclarationLowering {
    abstract fun isMergeableModule(declaration: ModuleDeclaration): Boolean

    private val loweredAliases = mutableSetOf<NameEntity>()

    override fun lowerTypeAliasDeclaration(declaration: TypeAliasDeclaration, owner: NodeOwner<ModuleDeclaration>?): TypeAliasDeclaration {
        val declarationResolved = if (loweredAliases.contains(declaration.aliasName)) {
            declaration.copy(aliasName = declaration.aliasName.replaceRightMost { oldName -> oldName + loweredAliases.size  })
        } else {
            declaration
        }
        loweredAliases.add(declarationResolved.aliasName)
        return super.lowerTypeAliasDeclaration(declarationResolved, owner)
    }

    @Suppress("UNCHECKED_CAST")
    override fun lowerTopLevelDeclarations(declarations: List<TopLevelDeclaration>, owner: NodeOwner<ModuleDeclaration>?): List<TopLevelDeclaration> {
        val extractedAliases = mutableListOf<TypeAliasDeclaration>()
        val declarationsResolved = declarations.flatMap { declaration ->
            if ((declaration is ModuleDeclaration) && (isMergeableModule(declaration))) {
                val (aliases, topLevels) = declaration.declarations.partition { it is TypeAliasDeclaration }
                extractedAliases.addAll(aliases as List<TypeAliasDeclaration>)
                listOf(declaration.copy(declarations = topLevels))
            } else {
                listOf(declaration)
            }
        }
        return super.lowerTopLevelDeclarations(extractedAliases + declarationsResolved, owner)
    }
}

private fun ModuleDeclaration.collectTopLevelData(): List<Triple<String, NameEntity, String>> {
    return declarations.filterIsInstance(MergeableDeclaration::class.java).flatMap { declaration ->
        val declarationName = declaration.getName()
        val record =
            if (declarationName == null) {
                emptyList()
            } else {
                listOf(Triple(uid, declarationName, declaration.uid))
            }
        if (declaration is ModuleDeclaration) {
            record + declaration.collectTopLevelData()
        } else {
            record
        }
    }
}

class MoveIllegalAliases : TsLowering {
    override fun lower(source: SourceSetDeclaration): SourceSetDeclaration {
        val bucket = source.sources.flatMap { sourceFile ->
            sourceFile.root.collectTopLevelData()
        }
        .groupBy { Pair(it.first, it.second) }
        .filterValues { it.size > 1 }
        .values.flatMap { it.map { data -> data.third }}.toSet()

        return source.copy(sources = source.sources.map { sourceFileDeclaration ->
            sourceFileDeclaration.copy(root = object: MoveIllegalAliasesLowering() {
                override fun isMergeableModule(declaration: ModuleDeclaration): Boolean {
                    return bucket.contains(declaration.uid)
                }
            } .lowerSourceDeclaration(sourceFileDeclaration.root))
        })
    }
}