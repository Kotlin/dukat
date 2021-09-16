package org.jetbrains.dukat.tsLowerings

import TopLevelDeclarationLowering
import org.jetbrains.dukat.ownerContext.NodeOwner
import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astCommon.QualifierEntity
import org.jetbrains.dukat.tsmodel.ClassLikeDeclaration
import org.jetbrains.dukat.tsmodel.EnumDeclaration
import org.jetbrains.dukat.tsmodel.ModuleDeclaration
import org.jetbrains.dukat.tsmodel.SourceSetDeclaration
import org.jetbrains.dukat.tsmodel.TopLevelDeclaration
import org.jetbrains.dukat.tsmodel.TypeAliasDeclaration
import org.jetbrains.dukat.tsmodel.VariableDeclaration

typealias UID = String
typealias LocalCollisions = Set<String>
typealias CollisionMap = MutableMap<UID, LocalCollisions>

private class KotlinStdlibCollisionCollectingLowering(val globalCollisionMap: CollisionMap) :
    TopLevelDeclarationLowering {
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
                    globalCollisionMap[moduleDeclaration.uid] = localCollisionSet
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

fun SourceSetDeclaration.collectionCollisionsWithStdlib(globalCollisionSet: CollisionMap): SourceSetDeclaration {
    return copy(sources = sources.map {
        it.copy(
            root = KotlinStdlibCollisionCollectingLowering(globalCollisionSet).lowerSourceDeclaration(
                it.root
            )
        )
    })
}

fun CollisionMap.getCollisionsForTheFile(moduleDeclaration: ModuleDeclaration): Set<String> {
    return get(moduleDeclaration.uid) ?: emptySet()
}

fun LocalCollisions.escapeCollisionFor(nameEntity: NameEntity): NameEntity {
    return if (nameEntity is IdentifierEntity && contains(nameEntity.value)) {
        QualifierEntity(IdentifierEntity("kotlin"), nameEntity)
    } else {
        nameEntity
    }
}