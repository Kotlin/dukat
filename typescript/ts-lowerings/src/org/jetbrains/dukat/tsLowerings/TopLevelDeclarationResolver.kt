package org.jetbrains.dukat.tsLowerings

import org.jetbrains.dukat.tsmodel.ClassLikeDeclaration
import org.jetbrains.dukat.tsmodel.ModuleDeclaration
import org.jetbrains.dukat.tsmodel.ReferenceDeclaration
import org.jetbrains.dukat.tsmodel.SourceSetDeclaration
import org.jetbrains.dukat.tsmodel.TopLevelDeclaration
import org.jetbrains.dukat.tsmodel.TypeAliasDeclaration
import org.jetbrains.dukat.tsmodel.VariableDeclaration
import org.jetbrains.dukat.tsmodel.WithReferenceDeclaration


private fun ModuleDeclaration.scan(topLevelDeclarationsMap: MutableMap<String, TopLevelDeclaration>) {
    this.declarations.forEach {
        when (it) {
            is VariableDeclaration -> {
                topLevelDeclarationsMap[it.uid] = it
            }
            is ClassLikeDeclaration -> {
                topLevelDeclarationsMap[it.uid] = it
            }
            is TypeAliasDeclaration -> {
                topLevelDeclarationsMap[it.uid] = it
            }
            is ModuleDeclaration -> {
                topLevelDeclarationsMap[it.uid] = it
                it.scan(topLevelDeclarationsMap)
            }
        }
    }
}

class TopLevelDeclarationResolver(private val sourceSetDeclaration: SourceSetDeclaration) : Iterable<TopLevelDeclaration> {
    private val declarationMap = buildMap<String, TopLevelDeclaration> {
        sourceSetDeclaration.sources.forEach { it.root.scan(this) }
    }

    override fun iterator(): Iterator<TopLevelDeclaration> {
        return declarationMap.values.iterator()
    }

    fun resolve(reference: ReferenceDeclaration?): TopLevelDeclaration? {
        return resolve(reference?.uid)
    }

    fun resolve(uid: String?): TopLevelDeclaration? {
        return declarationMap[uid]
    }

    fun resolveRecursive(uid: String?): TopLevelDeclaration? {
        return resolve(uid)?.let { resolvedEntity ->
            when (resolvedEntity) {
                is TypeAliasDeclaration -> {
                    val typeReference = resolvedEntity.typeReference
                    if (typeReference is WithReferenceDeclaration) {
                        resolveRecursive(typeReference.typeReference?.uid)
                    } else {
                        null
                    }
                }
                else -> resolvedEntity
            }
        }
    }
}