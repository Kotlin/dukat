package org.jetbrains.dukat.model.commonLowerings

import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astCommon.QualifierEntity
import org.jetbrains.dukat.astModel.CallableModel
import org.jetbrains.dukat.astModel.CallableParameterModel
import org.jetbrains.dukat.astModel.ClassLikeModel
import org.jetbrains.dukat.astModel.FunctionTypeModel
import org.jetbrains.dukat.astModel.LambdaParameterModel
import org.jetbrains.dukat.astModel.MemberModel
import org.jetbrains.dukat.astModel.MethodModel
import org.jetbrains.dukat.astModel.ParameterModel
import org.jetbrains.dukat.astModel.PropertyModel
import org.jetbrains.dukat.astModel.TypeModel
import org.jetbrains.dukat.astModel.TypeParameterModel
import org.jetbrains.dukat.astModel.TypeParameterReferenceModel
import org.jetbrains.dukat.astModel.TypeValueModel
import org.jetbrains.dukat.model.commonLowerings.overrides.InheritanceContext
import org.jetbrains.dukat.stdlib.KLIBROOT

private fun TypeModel.isAny(): Boolean {
    return this is TypeValueModel && value == IdentifierEntity("Any")
}

internal class OverrideTypeChecker(
    private val context: ModelContext,
    private val inheritanceContext: InheritanceContext,
    private val declaration: ClassLikeModel,
    private val parent: ClassLikeModel
) {

    private fun TypeModel.isDynamic(): Boolean {
        return (this is TypeValueModel && value == IdentifierEntity("dynamic"))
    }

    private fun TypeModel.resolveAsTypeParam(): TypeParameterModel? {
        return when (this) {
            is TypeParameterReferenceModel -> {
                declaration.typeParameters.firstOrNull {
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

    private fun TypeValueModel.isEquivalent(modelB: TypeValueModel): Boolean {
        val a = context.unalias(this)
        val b = context.unalias(modelB)

        return if ((a is TypeValueModel) && (b is TypeValueModel)) {
            a.namesAreEquivalent(b) && typeParamsAreEquivalent(a.params, b.params) && a.nullable == b.nullable
        } else {
            a.isEquivalent(b)
        }
    }

    private fun lambdaTypesAreEquivalent(paramsA: List<LambdaParameterModel>, paramsB: List<LambdaParameterModel>): Boolean {
        return compareLists(paramsA, paramsB) { a, b ->
            a.type.isEquivalent(b.type)
        }
    }

    private fun paramTypesAreEquivalent(paramsA: List<CallableParameterModel>, paramsB: List<CallableParameterModel>): Boolean {
        return compareLists(paramsA, paramsB) { a, b ->
            val varargsEquivalent = (a as? ParameterModel)?.vararg == (b as? ParameterModel)?.vararg
            varargsEquivalent && a.type.isEquivalent(b.type)
        }
    }

    private fun <T> compareLists(listA: List<T>, listB: List<T>, comparator: (a: T, b: T) -> Boolean): Boolean {
        if (listA.size != listB.size) {
            return false
        }

        return listA.zip(listB).all { (a, b) -> comparator(a, b) }
    }


    private fun paramTypesAreRelated(paramsA: List<CallableParameterModel>, paramsB: List<CallableParameterModel>): Boolean {
        return compareLists(paramsA, paramsB) { a, b ->
            if (a.type.isAny() || b.type.isAny()) {
                true
            } else {
                val classLikeA = a.type.resolveClassLike()
                val classLikeB = b.type.resolveClassLike()

                if (classLikeA != null && classLikeB != null) {
                    inheritanceContext.areRelated(classLikeA.classLike, classLikeB.classLike)
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
        val parametersAreEquivalent = lambdaTypesAreEquivalent(parameters, modelB.parameters)
        return parametersAreEquivalent && type.isOverridingReturnType(modelB.type)
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

    private fun CallableModel<*>.isOverriding(otherCallableModel: CallableModel<*>): MemberOverrideStatus {
        val parametersAreEquivalent = paramTypesAreEquivalent(parameters, otherCallableModel.parameters)
        val overridingReturnType = type.isOverridingReturnType(otherCallableModel.type)

        return if (parametersAreEquivalent && overridingReturnType) {
            MemberOverrideStatus.IS_OVERRIDE
        } else {
            if (paramTypesAreRelated(parameters, otherCallableModel.parameters) && overridingReturnType) {
                MemberOverrideStatus.IS_RELATED
            } else {
                MemberOverrideStatus.IS_NOT_OVERRIDE
            }
        }
    }


    private fun TypeModel.normalize(): TypeModel {
        return when (this) {
            is FunctionTypeModel -> copy(
                nullable = false,
                parameters = parameters.map { it.copy(name = null, type = it.type.normalize()) }
            )
            is TypeValueModel -> copy(nullable = false)
            else -> this
        }
    }

    private fun TypeModel.isOverridingReturnType(otherParameterType: TypeModel, box: TypeValueModel? = null): Boolean {
        val inbox = (box == null || box.value == IdentifierEntity("Array"))

        if (isEquivalent(otherParameterType) && inbox) {
            return true
        }

        if (otherParameterType is TypeParameterReferenceModel) {
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
                    if (isSameClass || inheritanceContext.isDescendant(classLikeA, classLikeB)) {
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

    fun MethodModel.isOverriding(otherMethodModel: MethodModel): MemberOverrideStatus {
        if (name != otherMethodModel.name) {
            return MemberOverrideStatus.IS_NOT_OVERRIDE
        }

        if (typeParameters.size != otherMethodModel.typeParameters.size) {
            return MemberOverrideStatus.IS_NOT_OVERRIDE
        }

        return (this as CallableModel<*>).isOverriding(otherMethodModel)
    }

    fun PropertyModel.isOverriding(otherPropertyModel: MemberModel): MemberOverrideStatus {
        if (otherPropertyModel !is PropertyModel) {
            return MemberOverrideStatus.IS_NOT_OVERRIDE
        }

        return if ((name == otherPropertyModel.name)) {
            if (type.isOverridingReturnType(otherPropertyModel.type)) {
                MemberOverrideStatus.IS_OVERRIDE
            } else {
                MemberOverrideStatus.IS_IMPOSSIBLE
            }
        } else {
            if ((type is FunctionTypeModel) && (otherPropertyModel.type is FunctionTypeModel)) {
                if ((type as CallableModel<*>).isOverriding(otherPropertyModel.type as CallableModel<*>) == MemberOverrideStatus.IS_RELATED) {
                    MemberOverrideStatus.IS_RELATED
                } else {
                    MemberOverrideStatus.IS_NOT_OVERRIDE
                }
            } else {
                MemberOverrideStatus.IS_NOT_OVERRIDE
            }
        }
    }

    fun PropertyModel.isImpossibleOverride(otherPropertyModel: MemberModel?): Boolean {
        if (otherPropertyModel !is PropertyModel) {
            return false
        }

        if ((type is TypeParameterReferenceModel)) {
            val classLikeA = type.resolveClassLike()?.classLike
            val classLikeB = otherPropertyModel.type.resolveClassLike()?.classLike

            return ((classLikeA != null) && (classLikeA === classLikeB)) || inheritanceContext.isDescendant(declaration, classLikeB)
        }
        return (!type.nullable && otherPropertyModel.type.nullable)
    }

    private fun TypeValueModel.namesAreEquivalent(typeModelB: TypeValueModel): Boolean {
        if (fqName == typeModelB.fqName) {
            return true
        }

        if (value.withLibPrefix() == typeModelB.fqName) {
            return true
        }

        if (typeModelB.value.withLibPrefix() == fqName) {
            return true
        }

        return false
    }

    private fun NameEntity.withLibPrefix(): NameEntity {
        return when (this) {
            is IdentifierEntity -> QualifierEntity(KLIBROOT, this)
            is QualifierEntity -> this
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

    private fun TypeParameterReferenceModel.isEquivalent(otherTypeParameter: TypeParameterReferenceModel): Boolean {
        val otherTypeParameterIndex = parent.typeParameters.indexOfFirst {
            val type = it.type
            type is TypeValueModel && type.value == otherTypeParameter.name
        }
        val relevantHeritage = declaration.parentEntities.firstOrNull {
            it.value.value == parent.name
        }
        return this == relevantHeritage?.typeParams?.getOrNull(otherTypeParameterIndex)
    }

    private fun TypeModel.isEquivalent(otherParameterType: TypeModel): Boolean {
        if (this == otherParameterType) {
            return true
        }

        if (isDynamic() || otherParameterType.isDynamic()) {
            return true
        }

        if ((this is TypeValueModel) && (otherParameterType is TypeParameterReferenceModel)) {
            return true
        }

        if ((this is TypeParameterReferenceModel) && (otherParameterType is TypeValueModel)) {
            this.resolveAsTypeParamConstraint()?.let {
                return it.isEquivalent(otherParameterType)
            }
        }

        if ((this is TypeParameterReferenceModel) && (otherParameterType is TypeParameterReferenceModel)) {
            return isEquivalent(otherParameterType)
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
}