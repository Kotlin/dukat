package org.jetbrains.dukat.idlLowerings

import org.jetbrains.dukat.idlDeclarations.*


private fun getFirstLevelParents(context: MissingMemberContext, declaration: IDLInterfaceDeclaration): List<IDLInterfaceDeclaration> {
    return declaration.parents.mapNotNull { context.resolveInterface(it.name) }
}

private fun getAllInterfaceParents(context: MissingMemberContext, declaration: IDLInterfaceDeclaration): List<IDLInterfaceDeclaration> {
    val firstLevelParents = getFirstLevelParents(context, declaration)
    return firstLevelParents + firstLevelParents.flatMap { getAllInterfaceParents(context, it) }
}


private class MissingMemberResolver(val context: MissingMemberContext) : IDLLowering {

    fun getAllDictionaryParents(declaration: IDLDictionaryDeclaration): List<IDLDictionaryDeclaration> {
        val firstLevelParents = declaration.parents.mapNotNull { context.resolveDictionary(it.name) }
        return firstLevelParents + firstLevelParents.flatMap { getAllDictionaryParents(it) }
    }

    fun IDLTypeDeclaration.isOverriding(parentType: IDLTypeDeclaration): Boolean {
        if (this == parentType) {
            return true
        }

        if (this is IDLSingleTypeDeclaration && parentType is IDLSingleTypeDeclaration) {
            val classLike = context.resolveInterface(name)
                    ?: context.resolveDictionary(name)
            val parentClassLike = context.resolveInterface(parentType.name)
                    ?: context.resolveDictionary(parentType.name)
            return when (classLike) {
                is IDLInterfaceDeclaration -> parentClassLike in getAllInterfaceParents(context, classLike)
                is IDLDictionaryDeclaration -> parentClassLike in getAllDictionaryParents(classLike)
                else -> false
            }
        }

        if (parentType is IDLUnionTypeDeclaration) {
            return parentType.unionMembers.any { isOverriding(it) }
        }

        if (parentType == IDLSingleTypeDeclaration("any", null, false)) {
            return true
        }

        return false
    }

    fun IDLDictionaryMemberDeclaration.isOverriding(parentMember: IDLDictionaryMemberDeclaration): Boolean {
        return name == parentMember.name
    }

    fun IDLAttributeDeclaration.isOverriding(parentMember: IDLAttributeDeclaration): Boolean {
        return name == parentMember.name &&
                static == parentMember.static &&
                type.isOverriding(parentMember.type)
    }

    fun IDLAttributeDeclaration.isConflicting(parentMember: IDLAttributeDeclaration): Boolean {
        return name == parentMember.name && !isOverriding(parentMember)
    }

    fun IDLOperationDeclaration.isOverriding(parentMember: IDLOperationDeclaration): Boolean {
        return name == parentMember.name &&
                static == parentMember.static &&
                arguments.size == parentMember.arguments.size &&
                (arguments.isEmpty() ||
                        !arguments.zip(parentMember.arguments) { a, b -> a.type.isOverriding(b.type) }.all { it })
    }

    fun IDLOperationDeclaration.isConflicting(parentMember: IDLOperationDeclaration): Boolean {
        return isOverriding(parentMember) && !returnType.isOverriding(parentMember.returnType)
    }

    fun IDLGetterDeclaration.isOverriding(parentMember: IDLGetterDeclaration): Boolean {
        return name == parentMember.name &&
                valueType.isOverriding(parentMember.valueType) &&
                key.type.isOverriding(parentMember.key.type)
    }

    fun IDLSetterDeclaration.isOverriding(parentMember: IDLSetterDeclaration): Boolean {
        return name == parentMember.name &&
                key.type.isOverriding(parentMember.key.type) &&
                value.type.isOverriding(parentMember.value.type)
    }

    override fun lowerInterfaceDeclaration(declaration: IDLInterfaceDeclaration): IDLInterfaceDeclaration {
        val allParents = getAllInterfaceParents(context, declaration)
        val newAttributes: MutableList<IDLAttributeDeclaration> = mutableListOf()
        val newOperations: MutableList<IDLOperationDeclaration> = mutableListOf()
        val conflictingAttributes: MutableList<IDLAttributeDeclaration> = mutableListOf()
        val conflictingOperations: MutableList<IDLOperationDeclaration> = mutableListOf()
        val conflictingGetters: MutableList<IDLGetterDeclaration> = mutableListOf()
        val conflictingSetters: MutableList<IDLSetterDeclaration> = mutableListOf()

        val conflictingParents: MutableList<IDLSingleTypeDeclaration> = mutableListOf()
        val allParentOperations: MutableList<IDLOperationDeclaration> = mutableListOf()
        val allParentAttributes: MutableList<IDLAttributeDeclaration> = mutableListOf()
        declaration.parents.forEach { parent ->
            val resolvedParent = context.resolveInterface(parent.name) ?: return@forEach
            var isConflicting = false
            val currentParentAttributes = (getAllInterfaceParents(context, resolvedParent) + resolvedParent).flatMap { it.attributes }
            val currentParentOperations = (getAllInterfaceParents(context, resolvedParent) + resolvedParent).flatMap { it.operations }
            allParentAttributes.forEach { attribute ->
                if (currentParentAttributes.any { it.isConflicting(attribute) }) {
                    isConflicting = true
                }
            }
            allParentOperations.forEach { operation ->
                if (currentParentOperations.any { it.isConflicting(operation) }) {
                    isConflicting = true
                }
            }
            if (isConflicting) {
                conflictingParents += parent
            } else {
                allParentAttributes += currentParentAttributes
                allParentOperations += currentParentOperations
            }
        }


        allParents.forEach { parent ->
            parent.operations.forEach { parentOperation ->
                if (declaration.operations.none { it.isOverriding(parentOperation) }) {
                    if (parentOperation.static || parent.kind == InterfaceKind.INTERFACE) {
                        newOperations += parentOperation.copy(
                                arguments = parentOperation.arguments.map {
                                    it.copy(
                                            type = if (it.defaultValue != null) {
                                                it.type.changeComment("= definedExternally")
                                            } else {
                                                it.type
                                            }
                                    )
                                },
                                override = true
                        )
                    }
                } else {
                    conflictingOperations += declaration.operations.filter { it.isConflicting(parentOperation) }
                }
            }
            parent.attributes.forEach { parentAttribute ->
                if (declaration.attributes.none { it.isOverriding(parentAttribute) }) {
                    if (declaration.attributes.none { it.name == parentAttribute.name }) {
                        if (parentAttribute.static || parent.kind == InterfaceKind.INTERFACE) {
                            newAttributes += parentAttribute.copy(override = true)
                        }
                    } else {
                        conflictingAttributes.addAll(declaration.attributes.filter { it.isConflicting(parentAttribute) })
                    }
                }
            }
            parent.getters.forEach { parentGetter ->
                conflictingGetters += declaration.getters.filter {
                    it.name == parentGetter.name && !it.isOverriding(parentGetter)
                }
            }
            parent.setters.forEach { parentSetter ->
                conflictingSetters += declaration.setters.filter {
                    it.name == parentSetter.name && !it.isOverriding(parentSetter)
                }
            }
        }
        if (declaration.kind == InterfaceKind.INTERFACE ||
                declaration.kind == InterfaceKind.ABSTRACT_CLASS) {
            newAttributes.removeIf { !it.static }
            newOperations.removeIf { !it.static }
        }
        return declaration.copy(
                attributes = declaration.attributes + newAttributes - conflictingAttributes,
                operations = declaration.operations + newOperations - conflictingOperations,
                getters = declaration.getters - conflictingGetters,
                setters = declaration.setters - conflictingSetters,
                parents = declaration.parents - conflictingParents
        )
    }

    override fun lowerDictionaryDeclaration(declaration: IDLDictionaryDeclaration): IDLDictionaryDeclaration {
        val newMembers = getAllDictionaryParents(declaration)
                .flatMap { it.members }.filter { parentMember ->
                    declaration.members.none { it.isOverriding(parentMember) }
                }.map { it.copy(inherited = true) }
        return declaration.copy(members = declaration.members + newMembers)
    }
}

private class DuplicateRemover(val context: MissingMemberContext) : IDLLowering {
    override fun lowerInterfaceDeclaration(declaration: IDLInterfaceDeclaration): IDLInterfaceDeclaration {
        val allParents = getAllInterfaceParents(context, declaration)
        val duplicatedAttributes: MutableList<IDLAttributeDeclaration> = mutableListOf()
        val duplicatedOperations: MutableList<IDLOperationDeclaration> = mutableListOf()
        allParents.forEach { parent ->
            parent.operations.filterNot { it.static }.forEach { parentOperation ->
                if (declaration.operations.any { it == parentOperation }) {
                    val (duplicated, _) = declaration.operations.partition {
                        it.name == parentOperation.name &&
                                it.returnType == parentOperation.returnType &&
                                it.arguments.size == parentOperation.arguments.size &&
                                it.arguments.zip(parentOperation.arguments).all { arguments ->
                                    arguments.first.type == arguments.second.type
                                } &&
                                it.override == parentOperation.override
                    }
                    duplicatedOperations += duplicated
                }
            }
            parent.attributes.filterNot { it.static }.forEach { parentAttribute ->
                val (duplicated, _) = declaration.attributes.partition {
                    it.name == parentAttribute.name &&
                            it.type == parentAttribute.type &&
                            it.readOnly == parentAttribute.readOnly &&
                            it.override == parentAttribute.override
                }
                duplicatedAttributes += duplicated
            }
        }
        return declaration.copy(
                attributes = declaration.attributes - duplicatedAttributes,
                operations = declaration.operations - duplicatedOperations
        )
    }

}

private class MissingMemberContext : IDLLowering {
    private val interfaces: MutableMap<String, IDLInterfaceDeclaration> = mutableMapOf()
    private val dictionaries: MutableMap<String, IDLDictionaryDeclaration> = mutableMapOf()

    override fun lowerInterfaceDeclaration(declaration: IDLInterfaceDeclaration): IDLInterfaceDeclaration {
        interfaces[declaration.name] = declaration
        return declaration
    }

    override fun lowerDictionaryDeclaration(declaration: IDLDictionaryDeclaration): IDLDictionaryDeclaration {
        dictionaries[declaration.name] = declaration
        return declaration
    }

    fun resolveInterface(name: String): IDLInterfaceDeclaration? = interfaces[name]

    fun resolveDictionary(name: String): IDLDictionaryDeclaration? = dictionaries[name]
}

fun IDLSourceSetDeclaration.addMissingMembers(): IDLSourceSetDeclaration {
    val context = MissingMemberContext()
    val newContext = MissingMemberContext()
    return DuplicateRemover(newContext).lowerSourceSetDeclaration(
            newContext.lowerSourceSetDeclaration(
                    MissingMemberResolver(context).lowerSourceSetDeclaration(
                            context.lowerSourceSetDeclaration(this)
                    )
            )
    )

}
