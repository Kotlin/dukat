package org.jetbrains.dukat.commonLowerings.merge

import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astModel.*
import org.jetbrains.dukat.panic.raiseConcern

private data class ClassLikeKey(
        val name: NameEntity,
        val typeParameters: List<TypeParameterModel>
)

private fun ClassModel.createKey(): ClassLikeKey {
    return ClassLikeKey(name, typeParameters)
}

private fun InterfaceModel.createKey(): ClassLikeKey {
    return ClassLikeKey(name, typeParameters)
}

private fun ClassLikeModel.createKey(): ClassLikeKey? {
    return when (this) {
        is ClassModel -> createKey()
        is InterfaceModel -> createKey()
        else -> null
    }
}

private fun ClassModel.mergeWithClass(otherClass: ClassModel): ClassModel {
    return copy(
            name = name,
            typeParameters = typeParameters,
            members = members + otherClass.members,
            companionObject = companionObject.mergeWith(otherClass.companionObject),
            parentEntities = parentEntities + otherClass.parentEntities
    )
}

private fun TypeModel.substituteUnit(): TypeModel {
    val returnsUnit = this is TypeValueModel && value == IdentifierEntity("Unit")
    return if (returnsUnit) {
        TypeValueModel(IdentifierEntity("@@None"), emptyList(), null)
    } else this
}

private fun ClassModel.mergeWithInterface(otherInterface: InterfaceModel): ClassModel {

    //TODO: should discuss why we are doing the at all
    val openInterfaceMembers = otherInterface.members.map { member ->
        when (member) {
            is PropertyModel -> member.copy(open = false, type = member.type.substituteUnit())
            is MethodModel ->
                member.copy(open = false, type = member.type.substituteUnit())
            else -> member
        }
    }

    return copy(
            name = name,
            typeParameters = typeParameters,
            members = members + openInterfaceMembers,
            companionObject = companionObject.mergeWith(otherInterface.companionObject),
            parentEntities = parentEntities + otherInterface.parentEntities
    )
}

private fun InterfaceModel.mergeWithInterface(otherInterface: InterfaceModel): InterfaceModel {
    return otherInterface.copy(
            name = name,
            typeParameters = typeParameters,
            members = members + otherInterface.members,
            companionObject = companionObject.mergeWith(otherInterface.companionObject),
            parentEntities = parentEntities + otherInterface.parentEntities
    )
}

private fun ClassLikeModel.merge(otherClassLike: ClassLikeModel): ClassLikeModel {
    return when (this) {
        is ClassModel -> when (otherClassLike) {
            is ClassModel -> mergeWithClass(otherClassLike)
            is InterfaceModel -> mergeWithInterface(otherClassLike)
            else -> raiseConcern("can not merge unknown ClassLikeModel implementation: ${this}") { this }
        }
        is InterfaceModel -> when (otherClassLike) {
            is ClassModel -> otherClassLike.mergeWithInterface(this)
            is InterfaceModel -> mergeWithInterface(otherClassLike)
            else -> raiseConcern("can not merge unknown ClassLikeModel implementation: ${this}") { this }
        }
        else -> raiseConcern("can not merge unknown ClassLikeModel implementation: ${this}") { this }
    }
}

fun ModuleModel.mergeClassesAndInterfaces(): ModuleModel {

    val classlikeBuckets = mutableMapOf<ClassLikeKey, MutableList<ClassLikeModel>>()

    declarations.forEach { declaration ->
        if (declaration is ClassLikeModel) {
            declaration.createKey()?.let { key ->
                classlikeBuckets.getOrPut(key) { mutableListOf() }.add(declaration)
            }
        }
    }


    val classlikeBucketsMerged = classlikeBuckets
            .mapValues { entry ->
                entry.value.reduceRight { classLikeModel, acc -> classLikeModel.merge(acc) }
            }.toMutableMap()

    val declarationResolved = mutableListOf<TopLevelModel>()
    declarations.forEach { declaration ->
        if (declaration is ClassLikeModel) {
            val classLikeModel = classlikeBucketsMerged.remove(declaration.createKey())
            if (classLikeModel != null) {
                declarationResolved.add(classLikeModel)
            }
        } else declarationResolved.add(declaration)
    }


    val submodulesResolved = submodules.map { submodule -> submodule.mergeClassesAndInterfaces() }

    return copy(
            declarations = declarationResolved,
            submodules = submodulesResolved
    )
}


fun SourceSetModel.mergeClassesAndInterfaces() = transform { it.mergeClassesAndInterfaces() }