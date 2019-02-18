package org.jetbrains.dukat.compiler.lowerings.merge

import org.jetbrains.dukat.ast.model.model.ClassLikeModel
import org.jetbrains.dukat.ast.model.model.ClassModel
import org.jetbrains.dukat.ast.model.model.InterfaceModel
import org.jetbrains.dukat.ast.model.model.ModuleModel
import org.jetbrains.dukat.ast.model.nodes.MethodNode
import org.jetbrains.dukat.ast.model.nodes.PropertyNode
import org.jetbrains.dukat.ast.model.nodes.TypeNode
import org.jetbrains.dukat.astCommon.TopLevelDeclaration
import org.jetbrains.dukat.tsmodel.TypeParameterDeclaration
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration

private data class ClassLikeKey(
    val name: String,
    val typeParameters: List<TypeParameterDeclaration>
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
        companionObject = companionObject.copy(members = companionObject.members + otherClass.companionObject.members),
        parentEntities = parentEntities + otherClass.parentEntities
    )
}

private fun ParameterValueDeclaration.substituteUnit(): ParameterValueDeclaration {
    val returnsUnit = this == TypeNode("Unit", emptyList())
    return if (returnsUnit) {
        TypeNode("@@None", emptyList())
    } else this
}

private fun ClassModel.mergeWithInterface(otherInterface: InterfaceModel): ClassModel {

    //TODO: should discuss why we are doing the at all
    val openInterfaceMembers = otherInterface.members.map { member ->
        when (member) {
            is PropertyNode -> member.copy(open = false, definedExternally = false, type = member.type.substituteUnit())
            is MethodNode -> member.copy(open = false, definedExternally = false, type = member.type.substituteUnit())
            else -> member
        }
    }

    return copy(
            name = name,
            typeParameters = typeParameters,
            members = members + openInterfaceMembers,
            companionObject = companionObject.copy(members = companionObject.members + otherInterface.companionObject.members),
            parentEntities = parentEntities + otherInterface.parentEntities
    )
}

private fun InterfaceModel.mergeWithInterface(otherInterface: InterfaceModel): InterfaceModel {
    return otherInterface.copy(
            name = name,
            typeParameters = typeParameters,
            members = members + otherInterface.members,
            companionObject = companionObject.copy(members = companionObject.members + otherInterface.companionObject.members),
            parentEntities = parentEntities + otherInterface.parentEntities
    )
}

private fun ClassLikeModel.merge(otherClassLike: ClassLikeModel): ClassLikeModel {
    return when(this) {
        is ClassModel -> when(otherClassLike) {
            is ClassModel -> mergeWithClass(otherClassLike)
            is InterfaceModel -> mergeWithInterface(otherClassLike)
            else -> throw Exception("can not merge unknown ClassLikeModel implementation: ${this}")
        }
        is InterfaceModel -> when(otherClassLike) {
            is ClassModel -> otherClassLike.mergeWithInterface(this)
            is InterfaceModel -> mergeWithInterface(otherClassLike)
            else -> throw Exception("can not merge unknown ClassLikeModel implementation: ${this}")
        }
        else -> throw Exception("can not merge unknown ClassLikeModel implementation: ${this}")
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
                entry.value.reduceRight {classLikeModel, acc -> classLikeModel.merge(acc) }
            }.toMutableMap()

    val declarationResolved = mutableListOf<TopLevelDeclaration>()
    declarations.forEach { declaration ->
        if (declaration is ClassLikeModel) {
            val classLikeModel = classlikeBucketsMerged.remove(declaration.createKey())
            if (classLikeModel != null) {
                declarationResolved.add(classLikeModel)
            }
        } else declarationResolved.add(declaration)
    }


    val submodulesResolved = sumbodules.map { submodule -> submodule.mergeClassesAndInterfaces() }

    return copy(
        declarations = declarationResolved,
        sumbodules = submodulesResolved
    )
}