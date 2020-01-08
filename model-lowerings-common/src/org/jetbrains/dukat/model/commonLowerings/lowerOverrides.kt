package org.jetbrains.dukat.model.commonLowerings

import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astCommon.appendLeft
import org.jetbrains.dukat.astModel.ClassLikeModel
import org.jetbrains.dukat.astModel.ClassModel
import org.jetbrains.dukat.astModel.FunctionTypeModel
import org.jetbrains.dukat.astModel.InterfaceModel
import org.jetbrains.dukat.astModel.MemberModel
import org.jetbrains.dukat.astModel.MethodModel
import org.jetbrains.dukat.astModel.ModuleModel
import org.jetbrains.dukat.astModel.ParameterModel
import org.jetbrains.dukat.astModel.PropertyModel
import org.jetbrains.dukat.astModel.SourceSetModel
import org.jetbrains.dukat.astModel.TopLevelModel
import org.jetbrains.dukat.astModel.TypeModel
import org.jetbrains.dukat.astModel.TypeParameterModel
import org.jetbrains.dukat.astModel.TypeParameterReferenceModel
import org.jetbrains.dukat.astModel.TypeValueModel
import org.jetbrains.dukat.astModel.transform

private fun TypeModel.isAny(): Boolean {
    return this is TypeValueModel && value == IdentifierEntity("Any")
}

private data class MemberData(val fqName: NameEntity?, val memberModel: MemberModel)

private class ClassLikeOverrideResolver(private val context: ModelContext, private val classLike: ClassLikeModel) {

    private fun TypeModel.resolveAsTypeParam(): TypeParameterModel? {
        return when(this) {
            is TypeParameterReferenceModel -> {
                classLike.typeParameters.firstOrNull { when (it.type) {
                  is TypeValueModel -> ((it.type as TypeValueModel).value == name)
                  else -> false
                }}
            }
            else -> null
        }
    }

    private fun TypeParameterReferenceModel.resolveAsTypeParamConstraint(): TypeModel? {
        return resolveAsTypeParam()?.constraints?.firstOrNull()
    }

    private fun ClassLikeModel.getKnownParents(): List<ResolvedClassLike<out ClassLikeModel>> {
        return parentEntities.flatMap { heritageModel ->
            val value = context.unalias(heritageModel.value)
            if (value is TypeValueModel) {
                value.resolveClassLike()?.let { resolvedClassLike ->
                    listOf(resolvedClassLike) + resolvedClassLike.classLike.getKnownParents()
                } ?: emptyList()
            } else {
                emptyList()
            }
        }
    }

    private fun MemberModel.lowerOverrides(
            allSuperDeclarations: Map<NameEntity?, List<MemberData>>
    ): MemberModel? {
        return when (this) {
            is MethodModel -> {
                val overridden = allSuperDeclarations[name]?.firstOrNull {
                    (it.memberModel is MethodModel) && (isOverriding(it.memberModel))
                }?.fqName ?:  if (isSpecialCase()) {
                    IdentifierEntity("Any")
                } else null

                if (overridden != null) {
                    copy(override = overridden, parameters = parameters.map { param -> param.copy(initializer = null) })
                } else {
                    this
                }
            }
            is PropertyModel -> {
                val overridden = allSuperDeclarations[name]?.firstOrNull {
                    (it.memberModel is PropertyModel) && isOverriding(it.memberModel)
                }
                if (isImpossibleOverride(overridden?.memberModel)) {
                    null
                } else {
                    copy(override = overridden?.fqName)
                }
            }
            is ClassLikeModel -> ClassLikeOverrideResolver(context,this).resolve()
            else -> this
        }
    }

    private fun ClassLikeModel.allParentMembers(): Map<NameEntity?, List<MemberData>> {
        val memberMap = mutableMapOf<NameEntity?, MutableList<MemberData>>()

        getKnownParents().forEach { resolvedClassLike ->
            resolvedClassLike.classLike.members.forEach {
                when (it) {
                    is MethodModel -> memberMap.getOrPut(it.name) { mutableListOf() }.add(MemberData(resolvedClassLike.fqName, it))
                    is PropertyModel -> memberMap.getOrPut(it.name) { mutableListOf() }.add(MemberData(resolvedClassLike.fqName, it))
                }
            }
        }

        return memberMap
    }

    private fun TypeModel.resolveClassLike(): ResolvedClassLike<out ClassLikeModel>? {
        return when(this) {
            is TypeValueModel -> resolveClassLike()
            is TypeParameterReferenceModel -> resolveAsTypeParamConstraint()?.resolveClassLike()
            else -> null
        }
    }

    private fun TypeValueModel.resolveClassLike(): ResolvedClassLike<out ClassLikeModel>? {
        return context.resolve(this.fqName)
    }

    private fun MethodModel.isOverriding(otherMethodModel: MethodModel): Boolean {
        if (name != otherMethodModel.name) {
            return false
        }

        if (typeParameters.size != otherMethodModel.typeParameters.size) {
            return false
        }

        val parametersAreEquivalent = paramsAreEquivalent(parameters, otherMethodModel.parameters)
        return parametersAreEquivalent && type.isOverriding(otherMethodModel.type)
    }

    private fun PropertyModel.isOverriding(otherPropertyModel: PropertyModel): Boolean {
        return (name == otherPropertyModel.name) && type.isOverriding(otherPropertyModel.type)
    }

    private fun PropertyModel.isImpossibleOverride(otherPropertyModel: MemberModel?): Boolean {
        if (otherPropertyModel !is PropertyModel) {
            return false
        }

        if ((type is TypeParameterReferenceModel)) {
            val classLikeA = type.resolveClassLike()?.classLike
            val classLikeB = otherPropertyModel.type.resolveClassLike()?.classLike

            return ((classLikeA != null) && (classLikeA === classLikeB)) || isDescendantOf(classLikeB)
        }
        return (!type.nullable && otherPropertyModel.type.nullable)
    }

    private fun MethodModel.isSpecialCase(): Boolean {
        val returnType = type

        if (name == IdentifierEntity("equals") && parameters.size == 1) {
            val firstParameterType = parameters[0].type
            if (firstParameterType is TypeValueModel && firstParameterType.value == IdentifierEntity("Any") && firstParameterType.nullable) {
                return true
            }
        }

        if (name == IdentifierEntity("hashCode") && parameters.isEmpty() &&
            returnType is TypeValueModel && returnType.value == IdentifierEntity("Number")) {
            return true
        }

        if (name == IdentifierEntity("toString") && parameters.isEmpty()) {
            return true
        }

        return false
    }

    private fun TypeModel.isDynamic(): Boolean {
        return (this is TypeValueModel && value == IdentifierEntity("dynamic"))
    }

    private fun TypeValueModel.isEquivalent(modelB: TypeValueModel): Boolean {
        val a = context.unalias(this)
        val b = context.unalias(modelB)

        return if ((a is TypeValueModel) && (b is TypeValueModel)) {
            a.fqName == b.fqName && typeParamsAreEquivalent(a.params, b.params) && a.nullable == b.nullable
        } else {
            a.isEquivalent(b)
        }
    }

    private fun typeParamsAreEquivalent(paramsA: List<TypeParameterModel>, paramsB: List<TypeParameterModel>): Boolean {
        if (paramsA.size != paramsB.size) {
            return false
        }

        paramsA.forEachIndexed { index, a ->
            if (!a.type.isEquivalent(paramsB[index].type)) {
                return false
            }
        }

        return true
    }

    private fun paramsAreEquivalent(paramsA: List<ParameterModel>, paramsB: List<ParameterModel>): Boolean {
        if (paramsA.size != paramsB.size) {
            return false
        }

        paramsA.forEachIndexed { index, a ->
            if (!a.type.isEquivalent(paramsB[index].type)) {
                return false
            }
        }

        return true
    }

    private fun FunctionTypeModel.isEquivalent(modelB: FunctionTypeModel): Boolean {
        val parametersAreEquivalent = paramsAreEquivalent(parameters, modelB.parameters)
        return parametersAreEquivalent && type.isOverriding(modelB.type)
    }

    private fun TypeModel.isEquivalent(otherParameterType: TypeModel): Boolean {
        if (this == otherParameterType) {
            return true
        }

        if (isDynamic() || otherParameterType.isDynamic()) {
            return true
        }

        if ((this is TypeParameterReferenceModel) && (otherParameterType is TypeValueModel)) {
            this.resolveAsTypeParamConstraint()?.let {
                return it.isEquivalent(otherParameterType)
            }
        }

        if ((this is TypeValueModel) && (otherParameterType is TypeValueModel)) {

            if (isEquivalent(otherParameterType)) {
                return true
            }
        }

        if ((this is FunctionTypeModel) && (otherParameterType is FunctionTypeModel)) {
            if (isEquivalent(otherParameterType)) {
                return true
            }
        }

        if ((this is FunctionTypeModel) && (otherParameterType is TypeValueModel)) {
            if (otherParameterType.value == IdentifierEntity("Function")) {
                return true
            }
        }

        return false
    }

    private fun isDescendantOf(otherClassLike: ClassLikeModel?): Boolean {
        return classLike.getKnownParents().map { it.classLike }.contains(otherClassLike)
    }

    private fun TypeModel.isOverriding(otherParameterType: TypeModel, box: TypeValueModel? = null): Boolean {
        val inbox = (box == null || box.value == IdentifierEntity("Array"))

        if (isEquivalent(otherParameterType) && inbox) {
            return true
        }

        if (otherParameterType.isAny()) {
            return if (!inbox) {
                this is TypeParameterReferenceModel
            } else {
                true
            }
        }

        if ((this is TypeValueModel) && (otherParameterType is TypeValueModel)) {
            val classLikeA = resolveClassLike()?.classLike
            val classLikeB = otherParameterType.resolveClassLike()?.classLike

            if ((classLikeA != null) && (classLikeB != null)) {
                val isSameClass = classLikeA === classLikeB
                val isParentClass = classLikeA.getKnownParents().any { it.classLike == classLikeB }

                if (params.isEmpty() && otherParameterType.params.isEmpty()) {
                    if (isSameClass || isParentClass) {
                        return true
                    }
                } else if (params.size == otherParameterType.params.size) {
                    if (isSameClass) {
                        return params.zip(otherParameterType.params).all { (paramA, paramB) ->
                            paramA.type.isOverriding(paramB.type, this)
                        }
                    }
                }
            } else {
                return fqName == otherParameterType.fqName
            }
        }

        return false
    }

    fun resolve(): ClassLikeModel {
        val parentMembers = classLike.allParentMembers()

        val membersLowered = classLike.members.mapNotNull { member ->
            member.lowerOverrides(parentMembers)
        }

        return when (classLike) {
            is InterfaceModel -> classLike.copy(members = membersLowered)
            is ClassModel -> classLike.copy(members = membersLowered)
            else -> classLike
        }
    }
}

private class OverrideResolver(private val context: ModelContext) {
    fun lowerOverrides(moduleModel: ModuleModel): ModuleModel {
        val loweredDeclarations = moduleModel.declarations.map { declaration ->
            when (declaration) {
                is ClassLikeModel -> ClassLikeOverrideResolver(context, declaration).resolve()
                else -> {
                    declaration
                }
            }
        }
        val loweredSubmodules = moduleModel.submodules.map { lowerOverrides(it) }
        return moduleModel.copy(declarations = loweredDeclarations, submodules = loweredSubmodules)
    }
}

private fun TopLevelModel.updateContext(context: ModelContext, ownerName: NameEntity) {
    context.register(this, ownerName)
    if (this is ClassLikeModel) {
        members.filterIsInstance(ClassLikeModel::class.java).forEach() {
            context.register(it, ownerName.appendLeft(name))
        }
    }
}

private fun ModuleModel.updateContext(context: ModelContext) {
    for (declaration in declarations) {
        declaration.updateContext(context, name)
    }

    submodules.forEach { declaration -> declaration.updateContext(context) }
}

private fun SourceSetModel.updateContext(astContext: ModelContext) {
    sources.map { source -> source.root.updateContext(astContext) }
}

fun SourceSetModel.lowerOverrides(stdlib: SourceSetModel?): SourceSetModel {
    val astContext = ModelContext()

    stdlib?.updateContext(astContext)
    updateContext(astContext)

    val overrideResolver = OverrideResolver(astContext)
    return transform {
        overrideResolver.lowerOverrides(it)
    }
}