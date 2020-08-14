package org.jetbrains.dukat.model.commonLowerings

import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astCommon.QualifierEntity
import org.jetbrains.dukat.astCommon.startsWith
import org.jetbrains.dukat.astModel.CallableModel
import org.jetbrains.dukat.astModel.CallableParameterModel
import org.jetbrains.dukat.astModel.ClassLikeModel
import org.jetbrains.dukat.astModel.ClassModel
import org.jetbrains.dukat.astModel.FunctionTypeModel
import org.jetbrains.dukat.astModel.InterfaceModel
import org.jetbrains.dukat.astModel.LambdaParameterModel
import org.jetbrains.dukat.astModel.MemberModel
import org.jetbrains.dukat.astModel.MethodModel
import org.jetbrains.dukat.astModel.ModuleModel
import org.jetbrains.dukat.astModel.NamedModel
import org.jetbrains.dukat.astModel.ParameterModel
import org.jetbrains.dukat.astModel.PropertyModel
import org.jetbrains.dukat.astModel.TypeModel
import org.jetbrains.dukat.astModel.TypeParameterModel
import org.jetbrains.dukat.astModel.TypeParameterReferenceModel
import org.jetbrains.dukat.astModel.TypeValueModel
import org.jetbrains.dukat.astModel.modifiers.InheritanceModifierModel
import org.jetbrains.dukat.graphs.Graph
import org.jetbrains.dukat.model.commonLowerings.overrides.InheritanceContext
import org.jetbrains.dukat.stdlib.KLIBROOT
import org.jetbrains.dukat.stdlib.TSLIBROOT
import org.jetbrains.dukat.stdlibGenerator.generated.stdlibClassMethodsMap

internal data class MemberData(val fqName: NameEntity?, val memberModel: MemberModel, val ownerModel: ClassLikeModel)

internal enum class MemberOverrideStatus {
    IS_OVERRIDE,
    IS_NOT_OVERRIDE,
    IS_RELATED,
    IS_IMPOSSIBLE
}

internal fun ClassLikeModel.getKnownParents(context: ModelContext): List<ResolvedClassLike<out ClassLikeModel>> {
    return context.getAllParents(this)
}

internal fun ClassLikeModel.allParentMembers(context: ModelContext): Map<NameEntity?, List<MemberData>> {
    val memberMap = mutableMapOf<NameEntity?, MutableList<MemberData>>()

    getKnownParents(context).forEach { resolvedClassLike ->
        resolvedClassLike.classLike.members.forEach {
            when (it) {
                is NamedModel -> {
                    if (!resolvedClassLike.existsOnlyInTsStdlib(it)) {
                        memberMap.getOrPut(it.name) { mutableListOf() }.add(MemberData(resolvedClassLike.fqName, it, resolvedClassLike.classLike))
                    }
                }
            }
        }
    }

    return memberMap
}

private fun <T : ClassLikeModel> ResolvedClassLike<T>.existsOnlyInTsStdlib(member: NamedModel): Boolean {
    return (fqName?.startsWith(TSLIBROOT) == true && (stdlibClassMethodsMap.containsKey(classLike.name)) && (stdlibClassMethodsMap[classLike.name]?.contains(member.name) == false))
}

private class ClassLikeOverrideResolver(
        private val context: ModelContext,
        private val inheritanceContext: InheritanceContext,
        private val classLike: ClassLikeModel
) {

    private fun MethodModel.toProperty(): PropertyModel {
        return PropertyModel(
            name = name,
            type = FunctionTypeModel(
                parameters = parameters.map {
                    LambdaParameterModel(
                        name = it.name,
                        type = it.type,
                        explicitlyDeclaredType = true
                    )
                },
                type = type,
                metaDescription = null
            ),
            typeParameters = typeParameters,
            static = static,
            override = null,
            immutable = false,
            initializer = null,
            getter = false,
            setter = false,
            open = open,
            explicitlyDeclaredType = true,
            lateinit = false
        )
    }

    private fun PropertyModel.toMethod(): MethodModel? {
        val type = type
        if (type !is FunctionTypeModel || type.parameters.any { it.name == null }) {
            return null
        }
        return MethodModel(
            name = name,
            parameters = type.parameters.map {
                ParameterModel(
                    name = it.name!!,
                    type = it.type,
                    initializer = null,
                    vararg = false,
                    modifier = null
                )
            },
            type = type.type,
            typeParameters = typeParameters,
            static = static,
            override = null,
            operator = false,
            annotations = listOf(),
            open = open,
            body = null
        )
    }

    private fun MethodModel.removeDefaultParamValues(override: NameEntity?): MethodModel {
        return copy(override = override, parameters = parameters.map { it.copy(initializer = null) })
    }

    private fun ClassLikeModel.isAbstractClass(): Boolean {
        return (this is ClassModel) && (this.inheritanceModifier == InheritanceModifierModel.ABSTRACT)
    }

    private fun MemberModel.lowerOverrides(
            allSuperDeclarations: Map<NameEntity?, List<MemberData>>
    ): List<MemberModel> {
        return when (this) {
            is MethodModel -> {
                val matchingLambdaProperty = allSuperDeclarations[name]?.firstOrNull {
                    val parentModel = it.memberModel
                    if (parentModel is PropertyModel) {
                        val parentModelToMethod = parentModel.toMethod()
                        (parentModelToMethod != null) && (with(OverrideTypeChecker(
                            context,
                            inheritanceContext,
                            classLike,
                            it.ownerModel
                        )) { isOverriding(parentModelToMethod) } == MemberOverrideStatus.IS_OVERRIDE)
                    } else {
                        false
                    }
                }?.fqName

                val overridden = allSuperDeclarations[name]?.firstOrNull {
                    (it.memberModel is MethodModel) && (with(OverrideTypeChecker(
                        context,
                        inheritanceContext,
                        classLike,
                        it.ownerModel
                    )) { isOverriding(it.memberModel) } == MemberOverrideStatus.IS_OVERRIDE)
                }?.fqName ?: if (isSpecialCase()) {
                    IdentifierEntity("Any")
                } else null

                when {
                    matchingLambdaProperty != null -> {
                        listOf(toProperty().copy(override = matchingLambdaProperty))
                    }
                    overridden != null -> {
                        listOf(copy(override = overridden, parameters = parameters.map { param -> param.copy(initializer = null) }))
                    }
                    else -> {
                        val related = allSuperDeclarations[name]?.firstOrNull {
                            (it.memberModel is MethodModel) && (with(OverrideTypeChecker(
                                context,
                                inheritanceContext,
                                classLike,
                                it.ownerModel
                            )) { isOverriding(it.memberModel) } == MemberOverrideStatus.IS_RELATED)
                        }

                        if ((related != null) && (classLike.isAbstractClass() != true)) {
                            listOf(this, (related.memberModel as MethodModel).removeDefaultParamValues(override = related.fqName))
                        } else {
                            listOf(this)
                        }

                    }
                }
            }
            is PropertyModel -> {
                val toMethod = toMethod()

                val matchingMethod = toMethod?.let { method ->
                    allSuperDeclarations[name]?.firstOrNull { parent ->
                        (parent.memberModel is MethodModel) &&
                                (with(OverrideTypeChecker(
                                    context,
                                    inheritanceContext,
                                    classLike,
                                    parent.ownerModel
                                )) { method.isOverriding(parent.memberModel) } == MemberOverrideStatus.IS_OVERRIDE)
                    }?.fqName
                }

                val overrideData = allSuperDeclarations[name]?.asSequence()?.map { Pair(it, with(OverrideTypeChecker(
                    context,
                    inheritanceContext,
                    classLike,
                    it.ownerModel
                )) { isOverriding(it.memberModel) }) }?.firstOrNull { (_, status) ->
                    (status == MemberOverrideStatus.IS_OVERRIDE) || (status == MemberOverrideStatus.IS_RELATED) || (status == MemberOverrideStatus.IS_IMPOSSIBLE)
                }

                val memberData = overrideData?.first

                if (matchingMethod != null) {
                    listOf(toMethod.copy(override = matchingMethod, parameters = toMethod.parameters.map { param -> param.copy(initializer = null) }))
                } else {
                    when (overrideData?.second) {
                        MemberOverrideStatus.IS_OVERRIDE -> {
                            if (with(OverrideTypeChecker(
                                    context,
                                    inheritanceContext,
                                    classLike,
                                    overrideData.first.ownerModel
                                )) { isImpossibleOverride(memberData?.memberModel) }) {
                                emptyList()
                            } else {
                                listOf(copy(override = memberData?.fqName))
                            }
                        }
                        MemberOverrideStatus.IS_IMPOSSIBLE -> emptyList()
                        MemberOverrideStatus.IS_RELATED -> emptyList()
                        else -> listOf(this)
                    }
                }
            }
            is ClassLikeModel -> listOf(ClassLikeOverrideResolver(context, inheritanceContext, this).resolve())
            else -> listOf(this)
        }
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

    fun resolve(): ClassLikeModel {
        val parentMembers = classLike.allParentMembers(context)

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

private class OverrideResolver(private val context: ModelContext, private val inheritanceContext: InheritanceContext) {
    fun lowerOverrides(moduleModel: ModuleModel): ModuleModel {
        val loweredDeclarations = moduleModel.declarations.map { declaration ->
            when (declaration) {
                is ClassLikeModel -> ClassLikeOverrideResolver(context, inheritanceContext, declaration).resolve()
                else -> {
                    declaration
                }
            }
        }
        val loweredSubmodules = moduleModel.submodules.map { lowerOverrides(it) }
        return moduleModel.copy(declarations = loweredDeclarations, submodules = loweredSubmodules)
    }
}

class LowerOverrides(modelContext: ModelContext, inheritanceContext: InheritanceContext) : ModuleModelContextAwareLowering(modelContext, inheritanceContext) {
    override fun lower(module: ModuleModel): ModuleModel {
        return OverrideResolver(modelContext, inheritanceContext).lowerOverrides(module)
    }
}