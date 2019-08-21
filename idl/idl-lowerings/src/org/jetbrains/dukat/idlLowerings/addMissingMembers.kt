package org.jetbrains.dukat.idlLowerings

import org.jetbrains.dukat.idlDeclarations.*

private class MissingMemberResolver(val context: MissingMemberContext) : IDLLowering {

    fun getFirstLevelParents(declaration: IDLInterfaceDeclaration): List<IDLInterfaceDeclaration> {
        return declaration.parents.mapNotNull { context.resolveInterface(it.name) }
    }

    fun getAllInterfaceParents(declaration: IDLInterfaceDeclaration): List<IDLInterfaceDeclaration> {
        val firstLevelParents = getFirstLevelParents(declaration)
        return firstLevelParents + firstLevelParents.flatMap { getAllInterfaceParents(it) }
    }

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
                is IDLInterfaceDeclaration -> parentClassLike in getAllInterfaceParents(classLike)
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

    private fun IDLInterfaceDeclaration.isInterface(): Boolean {
        return IDLSimpleExtendedAttributeDeclaration("NoInterfaceObject") in extendedAttributes
    }

    override fun lowerInterfaceDeclaration(declaration: IDLInterfaceDeclaration): IDLInterfaceDeclaration {
        val allParents = getAllInterfaceParents(declaration)
        val newAttributes: MutableList<IDLAttributeDeclaration> = mutableListOf()
        val newOperations: MutableList<IDLOperationDeclaration> = mutableListOf()
        val duplicatedAttributes: MutableList<IDLAttributeDeclaration> = mutableListOf()
        val duplicatedOperations: MutableList<IDLOperationDeclaration> = mutableListOf()
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
            val currentParentAttributes = (getAllInterfaceParents(resolvedParent) + resolvedParent).flatMap { it.attributes }
            val currentParentOperations = (getAllInterfaceParents(resolvedParent) + resolvedParent).flatMap { it.operations }
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
                    if (parentOperation.static || parent.isInterface()) {
                        newOperations += parentOperation.copy(
                                arguments = parentOperation.arguments.map {
                                    it.copy(
                                            type = if (it.defaultValue != null) {
                                                it.type.changeComment("= definedExternally")
                                            } else {
                                                it.type
                                            }
                                    )
                                }
                        )
                    }
                } else {
                    conflictingOperations += declaration.operations.filter { it.isConflicting(parentOperation) }
                }
                if (declaration.operations.any { it == parentOperation }) {
                    duplicatedOperations += parentOperation
                }
            }
            parent.attributes.forEach { parentAttribute ->
                if (declaration.attributes.none { it.isOverriding(parentAttribute) }) {
                    if (declaration.attributes.none { it.name == parentAttribute.name }) {
                        if (parentAttribute.static || parent.isInterface()) {
                            newAttributes += parentAttribute
                        }
                    } else {
                        conflictingAttributes.addAll(declaration.attributes.filter { it.isConflicting(parentAttribute) })
                    }
                }
                if (declaration.attributes.any { it == parentAttribute }) {
                    duplicatedAttributes += parentAttribute
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
        if (declaration.isInterface() ||
                (declaration.constructors.isEmpty() && declaration.primaryConstructor == null)) {
            newAttributes.removeIf { !it.static }
            newOperations.removeIf { !it.static }
        }
        return declaration.copy(
                attributes = declaration.attributes + newAttributes - duplicatedAttributes - conflictingAttributes,
                operations = declaration.operations + newOperations - duplicatedOperations - conflictingOperations,
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
    return MissingMemberResolver(context).lowerSourceSetDeclaration(
            context.lowerSourceSetDeclaration(this)
    )

}
