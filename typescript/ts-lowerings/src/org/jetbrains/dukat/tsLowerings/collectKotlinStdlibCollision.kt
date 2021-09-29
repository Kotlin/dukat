package org.jetbrains.dukat.tsLowerings

import TopLevelDeclarationLowering
import org.jetbrains.dukat.ownerContext.NodeOwner
import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.model.commonLowerings.CollisionContext
import org.jetbrains.dukat.model.commonLowerings.TranslationContext
import org.jetbrains.dukat.tsmodel.ClassLikeDeclaration
import org.jetbrains.dukat.tsmodel.EnumDeclaration
import org.jetbrains.dukat.tsmodel.ModuleDeclaration
import org.jetbrains.dukat.tsmodel.SourceSetDeclaration
import org.jetbrains.dukat.tsmodel.TopLevelDeclaration
import org.jetbrains.dukat.tsmodel.TypeAliasDeclaration
import org.jetbrains.dukat.tsmodel.VariableDeclaration

private class KotlinStdlibCollisionCollectingLowering(private val translationContext: TranslationContext): TopLevelDeclarationLowering {
    private val localCollisionSet = mutableSetOf<String>();

    // TODO: extend list of possible collisions
    private val stdLibFinalIdentifiers = setOf(
        "Any",
        "Boolean",
        "Nothing",
        "Number",
        "String"
    )

    override fun lowerSourceDeclaration(
        moduleDeclaration: ModuleDeclaration,
        owner: NodeOwner<ModuleDeclaration>?
    ): ModuleDeclaration {
        return super.lowerSourceDeclaration(moduleDeclaration, owner)
            .also {
                if (localCollisionSet.isNotEmpty()) {
                    translationContext.collisionContext.putFileCollision(moduleDeclaration.uid, localCollisionSet)
                }
            }
    }

    override fun lowerTopLevelDeclaration(
        declaration: TopLevelDeclaration,
        owner: NodeOwner<ModuleDeclaration>?
    ): TopLevelDeclaration? {
        val name = when (declaration) {
            is VariableDeclaration -> declaration.name
            is EnumDeclaration -> declaration.name
            is ClassLikeDeclaration -> (declaration.name as? IdentifierEntity)?.value
            is TypeAliasDeclaration -> (declaration.aliasName as? IdentifierEntity)?.value
            else -> null
        }
        return detectCollision(name) {
            super.lowerTopLevelDeclaration(declaration, owner)
        }
    }

    private fun String.isInCollisionWithStdlib(): Boolean {
        return stdLibFinalIdentifiers.contains(this);
    }

    private inline fun <R> detectCollision(name: String?, fn: () -> R): R {
        if (name != null && name.isInCollisionWithStdlib()) {
            localCollisionSet.add(name);
        }
        return fn()
    }
}

class CollectKotlinStdlibCollision(private val translationContext: TranslationContext) : TsLowering {
    init { translationContext.initCollisionContext(CollisionContext()) }

    override fun lower(source: SourceSetDeclaration): SourceSetDeclaration {
        return source.copy(sources = source.sources.map {
            it.copy(
                root = KotlinStdlibCollisionCollectingLowering(translationContext).lowerSourceDeclaration(
                    it.root
                )
            )
        })
    }
}