package org.jetbrains.dukat.model.commonLowerings

import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astCommon.hasPrefix
import org.jetbrains.dukat.astModel.ClassLikeModel
import org.jetbrains.dukat.astModel.ClassModel
import org.jetbrains.dukat.astModel.ObjectModel
import org.jetbrains.dukat.astModel.FunctionTypeModel
import org.jetbrains.dukat.astModel.InterfaceModel
import org.jetbrains.dukat.astModel.LambdaParameterModel
import org.jetbrains.dukat.astModel.MemberModel
import org.jetbrains.dukat.astModel.MethodModel
import org.jetbrains.dukat.astModel.ModuleModel
import org.jetbrains.dukat.astModel.NamedModel
import org.jetbrains.dukat.astModel.ParameterModel
import org.jetbrains.dukat.astModel.PropertyModel
import org.jetbrains.dukat.astModel.TypeValueModel
import org.jetbrains.dukat.astModel.modifiers.InheritanceModifierModel
import org.jetbrains.dukat.toposort.Graph
import org.jetbrains.dukat.model.commonLowerings.overrides.InheritanceContext
import org.jetbrains.dukat.stdlib.TSLIBROOT
import org.jetbrains.dukat.stdlibGenerator.generated.stdlibClassMethodsMap

private data class MemberData(val fqName: NameEntity?, val memberModel: MemberModel, val ownerModel: ClassLikeModel)

internal enum class MemberOverrideStatus {
    IS_OVERRIDE,
    IS_NOT_OVERRIDE,
    IS_RELATED,
    IS_IMPOSSIBLE
}

private fun ModelContext.buildInheritanceGraph(): Graph<ClassLikeModel> {
    val graph = Graph<ClassLikeModel>()

    getClassLikeIterable().forEach { classLike ->
        getAllParents(classLike).forEach { resolvedClassLike ->
            graph.addEdge(classLike, resolvedClassLike.classLike)
        }
    }

    return graph
}


private fun <T : ClassLikeModel> ResolvedClassLike<T>.existsOnlyInTsStdlib(member: NamedModel): Boolean {
    return (fqName?.hasPrefix(TSLIBROOT) == true && (stdlibClassMethodsMap.containsKey(classLike.name)) && (stdlibClassMethodsMap[classLike.name]?.contains(member.name) == false))
}

private class ClassLikeOverrideResolver(
        private val translationContext: TranslationContext,
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

    private fun ClassLikeModel.getKnownParents(): List<ResolvedClassLike<out ClassLikeModel>> {
        return translationContext.modelContext.getAllParents(this)
    }

    private fun MethodModel.removeDefaultParamValues(override: List<NameEntity>?): MethodModel {
        return copy(override = override, parameters = parameters.map { it.copy(initializer = null) })
    }

    private fun ClassLikeModel.isAbstractClass(): Boolean {
        return (this is ClassModel) && (this.inheritanceModifier == InheritanceModifierModel.ABSTRACT)
    }

    private fun MemberModel.lowerOverrides(
            allSuperDeclarations: Map<NameEntity?, List<MemberData>>,
            parents: List<NameEntity>
    ): List<MemberModel> {
        return when (this) {
            is MethodModel -> {
                val matchingLambdaProperty = allSuperDeclarations[name]?.filter {
                    val parentModel = it.memberModel
                    if (parentModel is PropertyModel) {
                        val parentModelToMethod = parentModel.toMethod()
                        (parentModelToMethod != null) && (with(OverrideTypeChecker(
                            translationContext,
                            classLike,
                            it.ownerModel
                        )) { isOverriding(parentModelToMethod) } == MemberOverrideStatus.IS_OVERRIDE)
                    } else {
                        false
                    }
                }?.filterIrrelevantParentMembers()?.mapNotNull { it.fqName }?.distinct()

                val overridden = allSuperDeclarations[name]?.filter {
                    (it.memberModel is MethodModel) && (with(OverrideTypeChecker(
                        translationContext,
                        classLike,
                        it.ownerModel
                    )) { isOverriding(it.memberModel) } == MemberOverrideStatus.IS_OVERRIDE)
                }?.filterIrrelevantParentMembers()?.mapNotNull { it.fqName }?.distinct() ?: if (isSpecialCase()) {
                    parents.ifEmpty { listOf(IdentifierEntity("Any")) }
                } else null

                when {
                    !matchingLambdaProperty.isNullOrEmpty() -> {
                        listOf(toProperty().copy(override = matchingLambdaProperty))
                    }
                    !overridden.isNullOrEmpty() -> {
                        listOf(copy(override = overridden, parameters = parameters.map { param -> param.copy(initializer = null) }))
                    }
                    else -> {
                        val related = allSuperDeclarations[name]?.filter {
                            (it.memberModel is MethodModel) && (with(OverrideTypeChecker(
                                translationContext,
                                classLike,
                                it.ownerModel
                            )) { isOverriding(it.memberModel) } == MemberOverrideStatus.IS_RELATED)
                        }

                        if (!related.isNullOrEmpty() && !classLike.isAbstractClass()) {
                            listOf(this) + related.map { relatedMethod ->
                                (relatedMethod.memberModel as MethodModel).removeDefaultParamValues(override = relatedMethod.fqName?.let { listOf(it) })
                            }
                        } else {
                            listOf(this)
                        }

                    }
                }
            }
            is PropertyModel -> {
                val toMethod = toMethod()

                val matchingMethods = toMethod?.let { method ->
                    allSuperDeclarations[name]?.filter { parent ->
                        (parent.memberModel is MethodModel) &&
                                (with(OverrideTypeChecker(
                                    translationContext,
                                    classLike,
                                    parent.ownerModel
                                )) { method.isOverriding(parent.memberModel) } == MemberOverrideStatus.IS_OVERRIDE)
                    }?.filterIrrelevantParentMembers()?.mapNotNull { it.fqName }?.distinct()
                }

                val overrideData = allSuperDeclarations[name]?.asSequence()?.map { Pair(it, with(OverrideTypeChecker(
                    translationContext,
                    classLike,
                    it.ownerModel
                )) { isOverriding(it.memberModel) }) }?.filter { (_, status) ->
                    (status == MemberOverrideStatus.IS_OVERRIDE) || (status == MemberOverrideStatus.IS_RELATED) || (status == MemberOverrideStatus.IS_IMPOSSIBLE)
                }?.filterIrrelevantParentMembersWithStatus()

                if (!matchingMethods.isNullOrEmpty()) {
                    listOf(toMethod.copy(override = matchingMethods, parameters = toMethod.parameters.map { param -> param.copy(initializer = null) }))
                } else {
                    var shouldBeDeleted = false
                    val overrides = overrideData?.mapNotNull { (method, status) ->
                        when (status) {
                            MemberOverrideStatus.IS_OVERRIDE -> {
                                if (with(
                                        OverrideTypeChecker(
                                            translationContext,
                                            classLike,
                                            method.ownerModel
                                        )
                                    ) { isImpossibleOverride(method.memberModel) }
                                ) {
                                    shouldBeDeleted = true
                                    null
                                } else {
                                    method.fqName
                                }
                            }
                            MemberOverrideStatus.IS_IMPOSSIBLE -> {
                                shouldBeDeleted = true
                                null
                            }
                            MemberOverrideStatus.IS_RELATED -> {
                                shouldBeDeleted = true
                                null
                            }
                            else -> null
                        }
                    }?.toList()?.distinct()
                    if (shouldBeDeleted) {
                        emptyList()
                    } else {
                        listOf(this.copy(override = overrides))
                    }
                }
            }
            is ClassLikeModel -> listOf(ClassLikeOverrideResolver(translationContext, this).resolve())
            else -> listOf(this)
        }
    }

    private fun List<MemberData>.filterIrrelevantParentMembers(): List<MemberData> {
        return filter { it.isRelevantParentMember(this) }
    }

    private fun Sequence<Pair<MemberData, MemberOverrideStatus>>.filterIrrelevantParentMembersWithStatus(): Sequence<Pair<MemberData, MemberOverrideStatus>> {
        return filter { (member, _) -> member.isRelevantParentMember(this.map { it.first }.toList()) }
    }

    private fun MemberData.isRelevantParentMember(otherMembers: List<MemberData>): Boolean {
        return when (memberModel) {
            is PropertyModel -> otherMembers.none { other ->
                other.memberModel is PropertyModel && translationContext.inheritanceContext.isDescendant(other.ownerModel, ownerModel)
            }
            is MethodModel -> otherMembers.none { other ->
                other.memberModel is MethodModel && translationContext.inheritanceContext.isDescendant(other.ownerModel, ownerModel)
            }
            else -> true
        }
    }

    private fun ClassLikeModel.allParentMembers(): Map<NameEntity?, List<MemberData>> {
        val memberMap = mutableMapOf<NameEntity?, MutableList<MemberData>>()

        getKnownParents().forEach { resolvedClassLike ->
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
        val parentMembers = classLike.allParentMembers()

        val membersLowered = classLike.members.flatMap { member ->
            member.lowerOverrides(parentMembers, translationContext.modelContext.getParents(classLike).mapNotNull { it.fqName })
        }

        val companionObject = classLike.companionObject?.let {
            ClassLikeOverrideResolver(translationContext, it).resolve() as ObjectModel
        }

        return when (classLike) {
            is InterfaceModel -> classLike.copy(members = membersLowered, companionObject = companionObject)
            is ClassModel -> classLike.copy(members = membersLowered, companionObject = companionObject)
            is ObjectModel -> classLike.copy(members = membersLowered)
            else -> classLike
        }
    }

}

private class OverrideResolver(private val translationContext: TranslationContext) {
    fun lowerOverrides(moduleModel: ModuleModel): ModuleModel {
        val loweredDeclarations = moduleModel.declarations.map { declaration ->
            when (declaration) {
                is ClassLikeModel -> ClassLikeOverrideResolver(translationContext, declaration).resolve()
                else -> {
                    declaration
                }
            }
        }
        val loweredSubmodules = moduleModel.submodules.map { lowerOverrides(it) }
        return moduleModel.copy(declarations = loweredDeclarations, submodules = loweredSubmodules)
    }
}

class LowerOverrides(private val translationContext: TranslationContext) : ModelLowering {

    override fun lower(module: ModuleModel): ModuleModel {
        return OverrideResolver(translationContext).lowerOverrides(module)
    }
}