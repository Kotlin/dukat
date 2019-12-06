package org.jetbrains.dukat.model.commonLowerings

import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astCommon.appendLeft
import org.jetbrains.dukat.astModel.ClassLikeModel
import org.jetbrains.dukat.astModel.ClassModel
import org.jetbrains.dukat.astModel.InterfaceModel
import org.jetbrains.dukat.astModel.TopLevelModel
import org.jetbrains.dukat.astModel.TypeAliasModel
import org.jetbrains.dukat.astModel.TypeModel
import org.jetbrains.dukat.astModel.TypeValueModel

data class ResolvedClassLike<T : ClassLikeModel>(
        val classLike: T,
        val fqName: NameEntity?
) {
    fun resolveFqName(): NameEntity {
        return fqName ?: classLike.name
    }
}

class ModelContext {
    private val myInterfaces: MutableMap<NameEntity, InterfaceModel> = mutableMapOf()
    private val myClassNodes: MutableMap<NameEntity, ClassModel> = mutableMapOf()
    private val myAliases: MutableMap<NameEntity, TypeAliasModel> = mutableMapOf()

    private fun registerInterface(interfaceDeclaration: InterfaceModel, ownerName: NameEntity) {
        myInterfaces[ownerName.appendLeft(interfaceDeclaration.name)] = interfaceDeclaration
    }

    private fun registerClass(classDeclaration: ClassModel, ownerName: NameEntity) {
        myClassNodes[ownerName.appendLeft(classDeclaration.name)] = classDeclaration
    }

    private fun registerAlias(typeAlias: TypeAliasModel, ownerName: NameEntity) {
        myAliases[ownerName.appendLeft(typeAlias.name)] = typeAlias
    }

    fun register(topLevelModel: TopLevelModel, ownerName: NameEntity) {
        when (topLevelModel) {
            is ClassModel -> registerClass(topLevelModel, ownerName)
            is InterfaceModel -> registerInterface(topLevelModel, ownerName)
            is TypeAliasModel -> registerAlias(topLevelModel, ownerName)
        }
    }

    fun unalias(typeModel: TypeValueModel): TypeModel {
        val typeResolved = myAliases[typeModel.fqName]?.typeReference?.let { typeReference ->
            if (typeReference is TypeValueModel) {
                unalias(typeReference)
            } else {
                typeReference
            }
        } ?: typeModel

        return if (typeResolved is TypeValueModel) {
            typeResolved.copy(params = typeResolved.params.map { param ->
                param.copy(
                        type = when (param.type) {
                            is TypeValueModel -> unalias(param.type as TypeValueModel)
                            else -> param.type
                        }
                )
            })
        } else {
            typeResolved
        }
    }

    private fun resolveInterface(name: NameEntity?): ResolvedClassLike<InterfaceModel>? {
        return myInterfaces[name]?.let {
            ResolvedClassLike(it, name)
        }
    }

    private fun resolveClass(name: NameEntity?): ResolvedClassLike<ClassModel>? {
        return myClassNodes[name]?.let {
            ResolvedClassLike(it, name)
        }
    }

    fun resolve(name: NameEntity?): ResolvedClassLike<out ClassLikeModel>? {
        return resolveInterface(name) ?: resolveClass(name)
    }
}