package org.jetbrains.dukat.idlLowerings

import org.jetbrains.dukat.idlDeclarations.IDLAttributeDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLDictionaryDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLFileDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLGetterDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLInterfaceDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLOperationDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLSetterDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLSingleTypeDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLSourceSetDeclaration
import org.jetbrains.dukat.idlDeclarations.InterfaceKind
import org.jetbrains.dukat.idlDeclarations.changeComment

private class MissingMemberResolver(val context: MissingMemberContext) : IDLLowering {

    val helper = OverrideHelper(context)

    override fun lowerInterfaceDeclaration(declaration: IDLInterfaceDeclaration, owner: IDLFileDeclaration): IDLInterfaceDeclaration {
        val allParents = helper.getAllInterfaceParents(declaration)
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
            val currentParentAttributes = (helper.getAllInterfaceParents(resolvedParent) + resolvedParent).flatMap { it.attributes }
            val currentParentOperations = (helper.getAllInterfaceParents(resolvedParent) + resolvedParent).flatMap { it.operations }
            allParentAttributes.forEach { attribute ->
                if (currentParentAttributes.any { helper.isConflicting(it, attribute) }) {
                    isConflicting = true
                }
            }
            allParentOperations.forEach { operation ->
                if (currentParentOperations.any { helper.isConflicting(it, operation) }) {
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
                if (declaration.operations.none {
                            helper.isOverriding(it, parentOperation) ||
                                    helper.isConflicting(it, parentOperation) ||
                                    helper.isConflicting(parentOperation, it)
                        }) {
                    if (parentOperation.static || parent.kind == InterfaceKind.INTERFACE) {
                        newOperations += parentOperation.copy(
                                arguments = parentOperation.arguments.map {
                                    it.copy(
                                            type = if (it.defaultValue != null || it.optional) {
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
                    conflictingOperations += declaration.operations.filter { helper.isConflicting(it, parentOperation) }
                }
            }
            parent.attributes.forEach { parentAttribute ->
                if (declaration.attributes.none {
                            helper.isOverriding(it, parentAttribute) ||
                                    helper.isConflicting(it, parentAttribute) ||
                                    helper.isConflicting(parentAttribute, it)
                        }) {
                    if (declaration.attributes.none { it.name == parentAttribute.name }) {
                        if (parentAttribute.static || parent.kind == InterfaceKind.INTERFACE) {
                            newAttributes += parentAttribute.copy(override = true)
                        }
                    }
                } else {
                    conflictingAttributes.addAll(declaration.attributes.filter { helper.isConflicting(it, parentAttribute) })
                }
            }
            parent.getters.forEach { parentGetter ->
                conflictingGetters += declaration.getters.filter {
                    it.name == parentGetter.name && !helper.isOverriding(it, parentGetter)
                }
            }
            parent.setters.forEach { parentSetter ->
                conflictingSetters += declaration.setters.filter {
                    it.name == parentSetter.name && !helper.isOverriding(it, parentSetter)
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

    override fun lowerDictionaryDeclaration(declaration: IDLDictionaryDeclaration, owner: IDLFileDeclaration): IDLDictionaryDeclaration {
        val newMembers = helper.getAllDictionaryParents(declaration)
                .flatMap { it.members }.filter { parentMember ->
                    declaration.members.none { helper.isOverriding(it, parentMember) }
                }.map { it.copy(inherited = true) }
        return declaration.copy(members = declaration.members + newMembers)
    }
}

private class DuplicateRemover(val context: MissingMemberContext) : IDLLowering {

    val helper = OverrideHelper(context)

    override fun lowerInterfaceDeclaration(declaration: IDLInterfaceDeclaration, owner: IDLFileDeclaration): IDLInterfaceDeclaration {
        val allParents = helper.getAllInterfaceParents(declaration)
        val duplicatedAttributes: MutableList<IDLAttributeDeclaration> = mutableListOf()
        val duplicatedOperations: MutableList<IDLOperationDeclaration> = mutableListOf()
        allParents.forEach { parent ->
            parent.operations.filterNot { it.static }.forEach { parentOperation ->
                if (declaration.operations.any { it == parentOperation }) {
                    val (duplicated, _) = declaration.operations.partition {
                        helper.isSimilar(it, parentOperation)
                    }
                    duplicatedOperations += duplicated
                }
            }
            parent.attributes.filterNot { it.static }.forEach { parentAttribute ->
                val (duplicated, _) = declaration.attributes.partition {
                    helper.isSimilar(it, parentAttribute)
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
