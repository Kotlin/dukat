package org.jetbrains.dukat.model.commonLowerings

import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.astModel.ClassLikeModel
import org.jetbrains.dukat.astModel.ClassModel
import org.jetbrains.dukat.astModel.InterfaceModel
import org.jetbrains.dukat.astModel.MemberModel
import org.jetbrains.dukat.astModel.MethodModel
import org.jetbrains.dukat.astModel.ModuleModel
import org.jetbrains.dukat.astModel.PropertyModel
import org.jetbrains.dukat.astModel.SourceSetModel
import org.jetbrains.dukat.astModel.TypeModel
import org.jetbrains.dukat.astModel.TypeParameterReferenceModel
import org.jetbrains.dukat.astModel.TypeValueModel
import org.jetbrains.dukat.astModel.transform
import org.jetbrains.dukat.panic.raiseConcern


private fun TypeModel.isAny(): Boolean {
    return this is TypeValueModel && value == IdentifierEntity("Any")
}

private class OverrideResolver(val context: ModelContext) {

    private fun ClassLikeModel.getKnownParents(): List<ClassLikeModel> {
        return when (this) {
            is InterfaceModel -> getKnownParents()
            is ClassModel -> getKnownParents()
            else -> raiseConcern("unknown ClassLikeDeclaration ${this}") { emptyList<ClassLikeModel>() }
        }
    }

    private fun TypeValueModel.resolveInterface(): InterfaceModel? {
        return context.resolveInterface(this.fqName)
    }

    private fun TypeValueModel.resolveClass(): ClassModel? {
        return context.resolveClass(this.fqName)
    }

    private fun TypeValueModel.resolveClassLike(): ClassLikeModel? {
        return resolveInterface() ?: resolveClass()
     }

    private fun InterfaceModel.getKnownParents(): List<InterfaceModel> {
        return parentEntities.flatMap { heritageModel ->
            heritageModel.value.resolveInterface()?.let { parentInterface ->
                listOf(parentInterface) + parentInterface.getKnownParents()
            } ?: emptyList()
        }
    }

    private fun ClassModel.getKnownParents(): List<ClassLikeModel> {
        return parentEntities.flatMap { heritageModel ->
            (heritageModel.value.resolveInterface() ?: heritageModel.value.resolveClass())?.let { parentModel ->
                listOf(parentModel) + parentModel.getKnownParents()
            } ?: emptyList()
        }
    }

    private fun ClassLikeModel.allParentMethods(): List<MethodModel> {
        return getKnownParents().flatMap { parentEntity ->
            parentEntity.members.filterIsInstance(MethodModel::class.java)
        }
    }

    private fun ClassLikeModel.allParentProperties(): List<PropertyModel> {
        return getKnownParents().flatMap { parentEntity ->
            parentEntity.members.filterIsInstance(PropertyModel::class.java)
        }
    }

    private fun MethodModel.isOverriding(otherMethodModel
                                         : MethodModel): Boolean {
        if (name != otherMethodModel.name) {
            return false
        }

        if (parameters.size != otherMethodModel.parameters.size) {
            return false
        }

        if (typeParameters.size != otherMethodModel.typeParameters.size) {
            return false
        }

        val parametersAreEquivalent = parameters
                .zip(otherMethodModel.parameters) { a, b ->
                    a.type.isEquivalent(b.type)
                }
                .all { it }

        return parametersAreEquivalent && type.isOverriding(otherMethodModel.type)
    }

    private fun PropertyModel.isOverriding(otherPropertyModel: PropertyModel): Boolean {
        return (name == otherPropertyModel.name) && type.isOverriding(otherPropertyModel.type)
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

    private fun TypeModel.isEquivalent(otherParameterType: TypeModel): Boolean {
        if (this == otherParameterType) {
            return true
        }

        if (isDynamic() || otherParameterType.isDynamic()) {
            return true
        }

        if ((this is TypeValueModel) && (otherParameterType is TypeValueModel)) {
            if (value == otherParameterType.value
                && params == otherParameterType.params
                && nullable == otherParameterType.nullable) {
                return true
            }
        }

        return false
    }


    private fun TypeModel.isOverriding(otherParameterType: TypeModel, inBox: Boolean = false): Boolean {
        if (isEquivalent(otherParameterType) && !inBox) {
            return true;
        }

        if (otherParameterType.isAny()) {
            return if (inBox) {
                this is TypeParameterReferenceModel
            } else {
                true
            }
        }

        if ((this is TypeValueModel) && (otherParameterType is TypeValueModel)) {
            val classLike: ClassLikeModel? = resolveClassLike()
            val otherClassLike: ClassLikeModel? = otherParameterType.resolveClassLike()


            val isSameClass = classLike != null && classLike === otherClassLike
            val isParentClass = classLike?.getKnownParents()?.contains(otherClassLike) == true

            if (params.isEmpty() && otherParameterType.params.isEmpty()) {
                if (isSameClass || isParentClass) {
                    return true
                }
            } else if (params.size == otherParameterType.params.size) {
                if (isSameClass) {
                    return params.zip(otherParameterType.params).all { (paramA, paramB) ->
                        paramA.type.isOverriding(paramB.type, true)
                    }
                }
            }
        }

        return false
    }

    private fun MemberModel.lowerOverrides(
            allSuperDeclarations: List<MethodModel>,
            allSuperProperties: List<PropertyModel>
    ): MemberModel {
        return when (this) {
            is MethodModel -> {
                val override =
                        allSuperDeclarations.any { superMethod ->
                            isOverriding(superMethod)
                        } || isSpecialCase()

                if (override) {
                    copy(override = override, parameters = parameters.map { param -> param.copy(initializer = null) })
                } else {
                    copy(override = override)
                }
            }
            is PropertyModel -> {
                val override = allSuperProperties.any { superMethod ->
                    isOverriding(superMethod)
                }
                copy(override = override)
            }
            else -> this
        }
    }

    private fun ClassLikeModel.lowerOverrides(): ClassLikeModel {
        val allParentMethods = allParentMethods()
        val allParentProperties = allParentProperties()

        val membersLowered = members.map {  member ->
            member.lowerOverrides(allParentMethods, allParentProperties)
        }

        return when (this) {
            is InterfaceModel -> copy(members = membersLowered)
            is ClassModel -> copy(members = membersLowered)
            else -> this
        }
    }

    fun lowerOverrides(moduleModel: ModuleModel): ModuleModel {
        val loweredDeclarations = moduleModel.declarations.map { declaration ->
            when (declaration) {
                is ClassLikeModel -> declaration.lowerOverrides()
                else -> {
                    declaration
                }
            }
        }
        val loweredSubmodules = moduleModel.submodules.map { lowerOverrides(it) }
        return moduleModel.copy(declarations = loweredDeclarations, submodules = loweredSubmodules)
    }
}

private fun ModuleModel.updateContext(context: ModelContext) {
    for (declaration in declarations) {
        if (declaration is InterfaceModel) {
            context.registerInterface(declaration, this)
        }
        if (declaration is ClassModel) {
            context.registerClass(declaration, this)
        }
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