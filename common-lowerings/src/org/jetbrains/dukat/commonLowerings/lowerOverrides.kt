package org.jetbrains.dukat.commonLowerings

import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.astModel.*
import org.jetbrains.dukat.panic.raiseConcern

private class OverrideResolver(val context: ModelContext) {

    private fun ClassLikeModel.getKnownParents(): List<ClassLikeModel> {
        return when (this) {
            is InterfaceModel -> getKnownParents()
            is ClassModel -> getKnownParents()
            else -> raiseConcern("unknown ClassLikeDeclaration ${this::class.simpleName}") { emptyList<ClassLikeModel>() }
        }
    }

    private fun InterfaceModel.getKnownParents(): List<InterfaceModel> {
        return parentEntities.flatMap { heritageModel ->
            val interfaceNode = context.resolveInterface(heritageModel.value.value)
            if (interfaceNode == null) {
                emptyList()
            } else {
                listOf(interfaceNode) + interfaceNode.getKnownParents()
            }
        }
    }

    private fun ClassModel.getKnownParents(): List<ClassLikeModel> {
        return parentEntities.flatMap {
            listOf(context.resolveInterface(it.value.value),
                    context.resolveClass(it.value.value))
        }.filterNotNull()
    }

    private fun InterfaceModel.allParentMethods(): List<MethodModel> {
        return getKnownParents().flatMap { parentEntity ->
            parentEntity.members.filterIsInstance(MethodModel::class.java) + parentEntity.allParentMethods()
        }
    }

    private fun InterfaceModel.allParentProperties(): List<PropertyModel> {
        return getKnownParents().flatMap { parentEntity ->
            parentEntity.members.filterIsInstance(PropertyModel::class.java) + parentEntity.allParentProperties()
        }
    }


    private fun ClassModel.allParentMethods(): List<MethodModel> {
        return getKnownParents().flatMap { parentEntity ->
            when (parentEntity) {
                is InterfaceModel -> parentEntity.members.filterIsInstance(MethodModel::class.java) + parentEntity.allParentMethods()
                is ClassModel -> parentEntity.members.filterIsInstance(MethodModel::class.java) + parentEntity.allParentMethods()
                else -> raiseConcern("unknown ClassLikeDeclaration $parentEntity") { emptyList<MethodModel>() }
            }
        }
    }

    private fun ClassModel.allParentProperties(): List<PropertyModel> {
        return getKnownParents().flatMap { parentEntity ->
            when (parentEntity) {
                is InterfaceModel -> parentEntity.members.filterIsInstance(PropertyModel::class.java) + parentEntity.allParentProperties()
                is ClassModel -> parentEntity.members.filterIsInstance(PropertyModel::class.java) + parentEntity.allParentProperties()
                else -> raiseConcern("unknown ClassLikeDeclaration $parentEntity") { emptyList<PropertyModel>() }
            }
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

        return parameters
                .zip(otherMethodModel.parameters) { a, b -> a.type.isOverriding(b.type) }
                .all { it }
    }

    private fun PropertyModel.isOverriding(otherPropertyModel: PropertyModel): Boolean {
        return (name == otherPropertyModel.name) && type.isOverriding(otherPropertyModel.type)
    }

    private fun MethodModel.isSpecialCase(): Boolean {

        val returnType = type

        if (name == IdentifierEntity("equals") && parameters.size == 1) {
            val firstParameterType = parameters[0].type
            if (firstParameterType is TypeValueModel && firstParameterType.value == IdentifierEntity("Any")) {
                return true
            }
        }

        if (name == IdentifierEntity("hashCode") && parameters.isEmpty() &&
                returnType is TypeValueModel && returnType.value == IdentifierEntity("Number")) {
            return true
        }

        if (name == IdentifierEntity("toString") && parameters.isEmpty() &&
                returnType is TypeValueModel && returnType.value == IdentifierEntity("String")) {
            return true
        }

        return false
    }

    private fun TypeModel.isOverriding(otherParameterType: TypeModel): Boolean {
        //TODO: we need to do this the right way
        if (this == otherParameterType) {
            return true
        }

        if (otherParameterType is TypeValueModel && otherParameterType.value == IdentifierEntity("dynamic")) {
            return true
        }

        if ((this is TypeValueModel) && (otherParameterType is TypeValueModel)) {

            val classLike: ClassLikeModel? = context.resolveClass(value) ?: context.resolveInterface(value)
            val otherClassLike: ClassLikeModel? = context.resolveClass(otherParameterType.value)
                    ?: context.resolveInterface(otherParameterType.value)

            if (classLike != null) {
                return classLike.getKnownParents().contains(otherClassLike)
            }
        }

        if (otherParameterType is TypeValueModel && otherParameterType.value == IdentifierEntity("Any")) {
            return true
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
                        allSuperDeclarations.any { superMethod -> isOverriding(superMethod) } || isSpecialCase()
                copy(override = override)
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

    fun lowerOverrides(moduleModel: ModuleModel): ModuleModel {
        val loweredDeclarations = moduleModel.declarations.map { declaration ->
            when (declaration) {
                is InterfaceModel -> {
                    val allParentMethods = declaration.allParentMethods()
                    val allParentProperties = declaration.allParentProperties()

                    declaration.copy(
                            members = declaration.members.map { member ->
                                member.lowerOverrides(allParentMethods, allParentProperties)
                            }
                    )
                }
                is ClassModel -> {
                    val allParentMethods = declaration.allParentMethods()
                    val allParentProperties = declaration.allParentProperties()

                    declaration.copy(
                            members = declaration.members.map { member ->
                                member.lowerOverrides(allParentMethods, allParentProperties)
                            }
                    )
                }
                else -> {
                    declaration
                }
            }
        }
        val loweredSubmodules = moduleModel.sumbodules.map { lowerOverrides(it) }
        return moduleModel.copy(declarations = loweredDeclarations, sumbodules = loweredSubmodules)
    }
}

private fun ModuleModel.updateContext(context: ModelContext): ModuleModel {
    for (declaration in declarations) {
        if (declaration is InterfaceModel) {
            context.registerInterface(declaration)
        }
        if (declaration is ClassModel) {
            context.registerClass(declaration)
        }
        if (declaration is ModuleModel) {
            declaration.updateContext(context)
        }
    }

    return this
}

private fun SourceSetModel.updateContext(astContext: ModelContext) = transform { it.updateContext(astContext) }


fun SourceSetModel.lowerOverrides(): SourceSetModel {
    val astContext = ModelContext()
    val overrideResolver = OverrideResolver(astContext)
    return updateContext(astContext).transform {
        overrideResolver.lowerOverrides(it)
    }
}