package org.jetbrains.dukat.model.commonLowerings

import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astCommon.appendLeft
import org.jetbrains.dukat.astModel.ClassLikeModel
import org.jetbrains.dukat.astModel.ClassModel
import org.jetbrains.dukat.astModel.FunctionTypeModel
import org.jetbrains.dukat.astModel.ImportModel
import org.jetbrains.dukat.astModel.InterfaceModel
import org.jetbrains.dukat.astModel.ModuleModel
import org.jetbrains.dukat.astModel.SourceSetModel
import org.jetbrains.dukat.astModel.TopLevelModel
import org.jetbrains.dukat.astModel.TypeAliasModel
import org.jetbrains.dukat.astModel.TypeModel
import org.jetbrains.dukat.astModel.TypeParameterReferenceModel
import org.jetbrains.dukat.astModel.TypeValueModel

data class ResolvedClassLike<T : ClassLikeModel>(
        val classLike: T,
        val fqName: NameEntity?
)

private fun SourceSetModel.forEachTopLevelModel(handler: (TopLevelModel, ownerName: NameEntity) -> Unit) {
    sources.forEach { sourceFile ->
        sourceFile.root.forEachTopLevelModel(handler)
    }
}

private fun ModuleModel.forEachTopLevelModel(handler: (TopLevelModel, ownerName: NameEntity) -> Unit) {
    declarations.forEach { topLevelModel ->
        handler(topLevelModel, name)
        if (topLevelModel is ClassLikeModel) {
            topLevelModel.members.filterIsInstance(TopLevelModel::class.java).forEach { handler(it, name.appendLeft(topLevelModel.name)) }
        }
    }

    submodules.forEach { submodule ->
        submodule.forEachTopLevelModel(handler)
    }

    imports.forEach { handler(it, name) }
}

class ModelContext(sourceSetModel: SourceSetModel) {
    private val myInterfaces: MutableMap<NameEntity, InterfaceModel> = mutableMapOf()
    private val myClassNodes: MutableMap<NameEntity, ClassModel> = mutableMapOf()
    private val myAliases: MutableMap<NameEntity, TypeAliasModel> = mutableMapOf()
    private val myNamedImports: MutableMap<NameEntity, NameEntity> = mutableMapOf()

    init {
        sourceSetModel.forEachTopLevelModel { topLevelModel, ownerName -> topLevelModel.register(ownerName) }
    }

    private fun ClassLikeModel.register(ownerName: NameEntity) {
        when (this) {
            is ClassModel -> registerClass(this, ownerName)
            is InterfaceModel -> registerInterface(this, ownerName)
        }
    }

    private fun ImportModel.register() {
        asAlias?.let {
            myNamedImports[it] = name
        }
    }

    private fun TopLevelModel.register(ownerName: NameEntity) {
        when (this) {
            is ClassLikeModel -> register(ownerName)
            is TypeAliasModel -> registerAlias(this, ownerName)
            is ImportModel -> register()
        }
    }

    private fun registerInterface(interfaceDeclaration: InterfaceModel, ownerName: NameEntity) {
        myInterfaces[ownerName.appendLeft(interfaceDeclaration.name)] = interfaceDeclaration
    }

    private fun registerClass(classDeclaration: ClassModel, ownerName: NameEntity) {
        myClassNodes[ownerName.appendLeft(classDeclaration.name)] = classDeclaration
    }

    private fun registerAlias(typeAlias: TypeAliasModel, ownerName: NameEntity) {
        myAliases[ownerName.appendLeft(typeAlias.name)] = typeAlias
    }

    fun unalias(typeModel: TypeModel, paramSubstitutions: Map<NameEntity, TypeModel> = mapOf()): TypeModel {
        val typeResolved = when (typeModel) {
            is TypeValueModel -> myAliases[typeModel.fqName]?.typeReference?.let { typeReference ->
                if (typeReference is TypeValueModel) {
                    unalias(typeReference, paramSubstitutions)
                } else {
                    typeReference
                }
            } ?: typeModel
            else -> typeModel
        }

        val newParamSubstitutions = when (typeModel) {
            is TypeValueModel -> {
                val aliasParamNames = myAliases[typeModel.fqName]?.typeParameters?.mapNotNull {
                    (it.type as? TypeValueModel)?.value
                }
                val actualParams = typeModel.params.map { it.type }
                aliasParamNames?.zip(actualParams)?.toMap() ?: emptyMap()
            }
            else -> emptyMap()
        }

        return when (typeResolved) {
            is TypeValueModel -> {
                typeResolved.copy(params = typeResolved.params.map { param ->
                    param.copy(type = unalias(param.type, paramSubstitutions + newParamSubstitutions))
                })
            }
            is FunctionTypeModel -> {
                typeResolved.copy(
                    parameters = typeResolved.parameters.map { parameter ->
                        parameter.copy(type = unalias(parameter.type, paramSubstitutions + newParamSubstitutions))
                    },
                    type = unalias(typeResolved.type, paramSubstitutions + newParamSubstitutions)
                )
            }
            is TypeParameterReferenceModel -> paramSubstitutions[typeResolved.name] ?: typeResolved
            else -> typeResolved
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

    private fun resolveImport(name: NameEntity?): ResolvedClassLike<out ClassLikeModel>? {
        return myNamedImports[name]?.let {
            resolveInterface(it) ?: resolveClass(it)
        }
    }

    fun resolve(name: NameEntity?): ResolvedClassLike<out ClassLikeModel>? {
        return resolveInterface(name) ?: resolveClass(name) ?: resolveImport(name)
    }

    fun getParents(classLikeModel: ClassLikeModel): List<ResolvedClassLike<out ClassLikeModel>> {
        return classLikeModel.parentEntities.mapNotNull { heritageModel ->
            val value = unalias(heritageModel.value)
            if (value is TypeValueModel) {
                resolve(value.fqName)
            } else null
        }
    }

    fun getAllParents(classLikeModel: ClassLikeModel): List<ResolvedClassLike<out ClassLikeModel>> {
        val immediateParents = getParents(classLikeModel)
        return immediateParents + immediateParents.flatMap { immediateParent -> getAllParents(immediateParent.classLike) }
    }

    fun getClassLikeIterable(): Iterable<ClassLikeModel> {
        return (myInterfaces.values + myClassNodes.values)
    }
}