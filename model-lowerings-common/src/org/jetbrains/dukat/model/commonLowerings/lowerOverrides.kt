package org.jetbrains.dukat.model.commonLowerings

import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.astCommon.NameEntity
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
import org.jetbrains.dukat.astModel.TypeModel
import org.jetbrains.dukat.astModel.TypeParameterModel
import org.jetbrains.dukat.astModel.TypeParameterReferenceModel
import org.jetbrains.dukat.astModel.TypeValueModel
import org.jetbrains.dukat.astModel.transform

private fun TypeModel.isAny(): Boolean {
    return this is TypeValueModel && value == IdentifierEntity("Any")
}

private data class MemberData(val fqName: NameEntity?, val memberModel: MemberModel, val ownerModel: ClassLikeModel)

private enum class MethodOverrideStatus {
    IS_OVERRIDE,
    IS_NOT_OVERRIDE,
    IS_RELATED
}

private class ClassLikeOverrideResolver(private val context: ModelContext, private val classLike: ClassLikeModel) {

    private fun TypeModel.resolveAsTypeParam(): TypeParameterModel? {
        return when (this) {
            is TypeParameterReferenceModel -> {
                classLike.typeParameters.firstOrNull {
                    when (it.type) {
                        is TypeValueModel -> ((it.type as TypeValueModel).value == name)
                        else -> false
                    }
                }
            }
            else -> null
        }
    }

    private fun TypeParameterReferenceModel.resolveAsTypeParamConstraint(): TypeModel? {
        return resolveAsTypeParam()?.constraints?.firstOrNull()
    }

    private fun ClassLikeModel.getKnownParents(): List<ResolvedClassLike<out ClassLikeModel>> {
        return context.getAllParents(this)
    }

    private fun MethodModel.removeDefaultParamValues(override: NameEntity?): MethodModel {
        return copy(override = override, parameters = parameters.map { it.copy(initializer = null) })
    }

    private fun ClassLikeModel.isAbstractClass(): Boolean {
        return (this is ClassModel) && (this.abstract)
    }

    private fun MemberModel.lowerOverrides(
            allSuperDeclarations: Map<NameEntity?, List<MemberData>>
    ): List<MemberModel> {
        return when (this) {
            is MethodModel -> {
                val overridden = allSuperDeclarations[name]?.firstOrNull {
                    (it.memberModel is MethodModel) && (isOverriding(it.memberModel) == MethodOverrideStatus.IS_OVERRIDE)
                }?.fqName ?: if (isSpecialCase()) {
                    IdentifierEntity("Any")
                } else null

                if (overridden != null) {
                    listOf(copy(override = overridden, parameters = parameters.map { param -> param.copy(initializer = null) }))
                } else {
                    val related = allSuperDeclarations[name]?.firstOrNull {
                        (it.memberModel is MethodModel) && (isOverriding(it.memberModel) == MethodOverrideStatus.IS_RELATED)
                    }

                    val ownerModel = related?.ownerModel

                    if ((related != null) && (ownerModel?.isAbstractClass() != true)) {
                        listOf(this, (related.memberModel as MethodModel).removeDefaultParamValues(override = related.fqName))
                    } else {
                        listOf(this)
                    }

                }
            }
            is PropertyModel -> {
                val overridden = allSuperDeclarations[name]?.firstOrNull {
                    (it.memberModel is PropertyModel) && isOverriding(it.memberModel)
                }
                if (isImpossibleOverride(overridden?.memberModel)) {
                    emptyList()
                } else {
                    listOf(copy(override = overridden?.fqName))
                }
            }
            is ClassLikeModel -> listOf(ClassLikeOverrideResolver(context, this).resolve())
            else -> listOf(this)
        }
    }

    private fun ClassLikeModel.allParentMembers(): Map<NameEntity?, List<MemberData>> {
        val memberMap = mutableMapOf<NameEntity?, MutableList<MemberData>>()

        getKnownParents().forEach { resolvedClassLike ->
            resolvedClassLike.classLike.members.forEach {
                when (it) {
                    is MethodModel -> memberMap.getOrPut(it.name) { mutableListOf() }.add(MemberData(resolvedClassLike.fqName, it, this))
                    is PropertyModel -> memberMap.getOrPut(it.name) { mutableListOf() }.add(MemberData(resolvedClassLike.fqName, it, this))
                }
            }
        }

        return memberMap
    }

    private fun TypeModel.resolveClassLike(): ResolvedClassLike<out ClassLikeModel>? {
        return when (this) {
            is TypeValueModel -> resolveClassLike()
            is TypeParameterReferenceModel -> resolveAsTypeParamConstraint()?.resolveClassLike()
            else -> null
        }
    }

    private fun TypeValueModel.resolveClassLike(): ResolvedClassLike<out ClassLikeModel>? {
        return context.resolve(this.fqName)
    }

    private fun MethodModel.isOverriding(otherMethodModel: MethodModel): MethodOverrideStatus {
        if (name != otherMethodModel.name) {
            return MethodOverrideStatus.IS_NOT_OVERRIDE
        }

        if (typeParameters.size != otherMethodModel.typeParameters.size) {
            return MethodOverrideStatus.IS_NOT_OVERRIDE
        }


        val parametersAreEquivalent = paramTypesAreEquivalent(parameters, otherMethodModel.parameters)
        val overridingReturnType = type.isOverridingReturnType(otherMethodModel.type)

        return if (parametersAreEquivalent && overridingReturnType) {
            MethodOverrideStatus.IS_OVERRIDE
        } else {
            if (paramTypesAreRelated(parameters, otherMethodModel.parameters) && overridingReturnType) {
                MethodOverrideStatus.IS_RELATED
            } else {
                MethodOverrideStatus.IS_NOT_OVERRIDE
            }
        }
    }

    private fun PropertyModel.isOverriding(otherPropertyModel: PropertyModel): Boolean {
        return (name == otherPropertyModel.name) && type.isOverridingReturnType(otherPropertyModel.type)
    }

    private fun PropertyModel.isImpossibleOverride(otherPropertyModel: MemberModel?): Boolean {
        if (otherPropertyModel !is PropertyModel) {
            return false
        }

        if ((type is TypeParameterReferenceModel)) {
            val classLikeA = type.resolveClassLike()?.classLike
            val classLikeB = otherPropertyModel.type.resolveClassLike()?.classLike

            return ((classLikeA != null) && (classLikeA === classLikeB)) || context.inheritanceContext.isDescendant(classLike, classLikeB)
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

    private fun <T> compareLists(listA: List<T>, listB: List<T>, comparator: (a: T, b: T) -> Boolean): Boolean {
        if (listA.size != listB.size) {
            return false
        }

        return listA.zip(listB).all { (a, b) -> comparator(a, b) }
    }

    private fun paramTypesAreEquivalent(paramsA: List<ParameterModel>, paramsB: List<ParameterModel>): Boolean {
        return compareLists(paramsA, paramsB) { a, b ->
            a.type.isEquivalent(b.type)
        }
    }

    private fun TypeModel.normalize(): TypeModel {
        return when (this) {
            is FunctionTypeModel -> copy(
                    nullable = false,
                    parameters = parameters.map { it.copy(name = "_", type = it.type.normalize(), initializer = null) }
            )
            is TypeValueModel -> copy(nullable = false)
            else -> this
        }
    }

    private fun paramTypesAreRelated(paramsA: List<ParameterModel>, paramsB: List<ParameterModel>): Boolean {
        return compareLists(paramsA, paramsB) { a, b ->
            if (a.type.isAny() || b.type.isAny()) {
                true
            } else {
                val classLikeA = a.type.resolveClassLike()
                val classLikeB = b.type.resolveClassLike()

                if (classLikeA != null && classLikeB != null) {
                    context.inheritanceContext.areRelated(classLikeA.classLike, classLikeB.classLike)
                } else {
                    val aType = a.type.normalize()
                    val bType = b.type.normalize()

                    if ((aType is TypeValueModel) && (bType is TypeValueModel)) {
                        (aType.fqName != null) && aType.fqName == bType.fqName
                    } else {
                        aType == bType
                    }
                }
            }
        }
    }

    private fun FunctionTypeModel.isEquivalent(modelB: FunctionTypeModel): Boolean {
        val parametersAreEquivalent = paramTypesAreEquivalent(parameters, modelB.parameters)
        return parametersAreEquivalent && type.isOverridingReturnType(modelB.type)
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

    private fun TypeModel.isOverridingReturnType(otherParameterType: TypeModel, box: TypeValueModel? = null): Boolean {
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

                if (params.isEmpty() && otherParameterType.params.isEmpty()) {
                    if (isSameClass || context.inheritanceContext.isDescendant(classLikeA, classLikeB)) {
                        return true
                    }
                } else if (params.size == otherParameterType.params.size) {
                    if (isSameClass) {
                        return params.zip(otherParameterType.params).all { (paramA, paramB) ->
                            paramA.type.isOverridingReturnType(paramB.type, this)
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

        val membersLowered = classLike.members.flatMap { member ->
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

fun SourceSetModel.lowerOverrides(stdlib: SourceSetModel?): SourceSetModel {
    val modelContext = ModelContext(stdlib, this)

    val overrideResolver = OverrideResolver(modelContext)

    return transform {
        overrideResolver.lowerOverrides(it)
    }
}