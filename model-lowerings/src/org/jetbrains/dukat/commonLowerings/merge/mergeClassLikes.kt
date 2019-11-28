package org.jetbrains.dukat.commonLowerings.merge

import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astCommon.appendLeft
import org.jetbrains.dukat.astModel.ClassLikeModel
import org.jetbrains.dukat.astModel.ClassModel
import org.jetbrains.dukat.astModel.HeritageModel
import org.jetbrains.dukat.astModel.InterfaceModel
import org.jetbrains.dukat.astModel.ModuleModel
import org.jetbrains.dukat.astModel.ObjectModel
import org.jetbrains.dukat.astModel.SourceSetModel
import org.jetbrains.dukat.astModel.modifiers.VisibilityModifierModel
import org.jetbrains.dukat.commonLowerings.merge.processing.ModelWithOwnerName
import org.jetbrains.dukat.commonLowerings.merge.processing.fetchClassLikes
import org.jetbrains.dukat.panic.raiseConcern

private fun mergeParentEntities(parentEntitiesA: List<HeritageModel>, parentEntitiesB: List<HeritageModel>): List<HeritageModel> {
    val parentSet = parentEntitiesA.toSet()
    return parentEntitiesA + parentEntitiesB.filter { parentEntity -> !parentSet.contains(parentEntity) }
}

private operator fun ObjectModel?.plus(otherModel: ObjectModel?): ObjectModel? {
    if (otherModel == null) {
        return this
    }
    if (this == null) {
        return ObjectModel(
                IdentifierEntity(""),
                otherModel.members,
                listOf(),
                VisibilityModifierModel.DEFAULT,
                null
        )
    }

    return copy(members = otherModel.members + members)
}

private operator fun InterfaceModel.plus(b: ClassLikeModel): InterfaceModel {
    // according to the spec members are applied in reversed order - https://www.typescriptlang.org/docs/handbook/declaration-merging.html
    return copy(
            members = b.members + members,
            typeParameters = typeParameters,
            companionObject = companionObject + b.companionObject,
            parentEntities = mergeParentEntities(parentEntities, b.parentEntities)
    )
}

private operator fun ClassModel.plus(b: ClassLikeModel): ClassModel {
    // according to the spec members are applied in reversed order - https://www.typescriptlang.org/docs/handbook/declaration-merging.html
    return copy(
            members = b.members + members,
            typeParameters = typeParameters,
            companionObject = companionObject + b.companionObject,
            parentEntities = mergeParentEntities(parentEntities, b.parentEntities)
    )
}

private operator fun ClassLikeModel.plus(b: ClassLikeModel): ClassLikeModel {
    val a = this
    return when (a) {
        is InterfaceModel -> when (b) {
            is InterfaceModel -> a + b
            is ClassModel -> b + a
            else -> a
        }
        is ClassModel -> when (b) {
            is InterfaceModel -> a + b
            is ClassModel -> a + b
            else -> raiseConcern("can not merge unknown ClassLikeModel implementation: ${a}") { a }
        }
        else -> a
    }
}

private operator fun ModelWithOwnerName<ClassLikeModel>.plus(b: ModelWithOwnerName<ClassLikeModel>): ModelWithOwnerName<ClassLikeModel> {
    return copy(model = model + b.model)
}

private fun ModuleModel.mergeClassLikes(bucket: Map<NameEntity, ClassLikeModel>, alreadyMerged: MutableSet<NameEntity> = mutableSetOf()): ModuleModel {
    val declarationsMerged = declarations.mapNotNull {
        if (it is ClassLikeModel) {
            val key = name.appendLeft(it.name)
            if (alreadyMerged.contains(key)) {
                null
            } else if (bucket.containsKey(key)) {
                alreadyMerged.add(key)
                bucket[key]
            } else {
                it
            }
        } else {
            it
        }
    }

    return copy(declarations = declarationsMerged, submodules = submodules.map { it.mergeClassLikes(bucket) })
}

fun SourceSetModel.mergeClassLikes(): SourceSetModel {
    val interfaces = sources.flatMap { source -> source.root.fetchClassLikes() }
    val bucket = interfaces
            .groupBy { it.ownerName }
            .mapValues { (_, items) -> items.reduce { a, b -> a + b }.model }

    return copy(sources = sources.map { source -> source.copy(root = source.root.mergeClassLikes(bucket)) })
}