package org.jetbrains.dukat.idlLowerings

import org.jetbrains.dukat.idlDeclarations.*

private class OverrideResolver(val context: OverrideContext) : IDLLowering {

    fun getAllParents(declaration: IDLInterfaceDeclaration): List<IDLInterfaceDeclaration> {
        val firstLevelParents = declaration.parents.mapNotNull { context.resolveInterface(it.name) }
        return firstLevelParents + firstLevelParents.flatMap { getAllParents(it) }
    }

    fun IDLTypeDeclaration.isOverriding(parentType: IDLTypeDeclaration): Boolean {
        return this == parentType
    }

    fun IDLAttributeDeclaration.isOverriding(parentMember: IDLAttributeDeclaration): Boolean {
        return name == parentMember.name &&
                type.isOverriding(parentMember.type) &&
                static == parentMember.static
    }

    fun IDLOperationDeclaration.isOverriding(parentMember: IDLOperationDeclaration): Boolean {
        return name == parentMember.name &&
                static == parentMember.static &&
                arguments.size == parentMember.arguments.size &&
                !arguments.zip(parentMember.arguments) { a, b -> a.type.isOverriding(b.type) }.all { it }
    }

    override fun lowerInterfaceDeclaration(declaration: IDLInterfaceDeclaration): IDLInterfaceDeclaration {
        if (IDLSimpleExtendedAttributeDeclaration("NoInterfaceObject") in declaration.extendedAttributes
                || declaration.constructors.isEmpty()) {
            //if this is interface or abstract class, it should not implement parent members
            return declaration
        }
        val allParents = getAllParents(declaration)
        val interfaceParents = allParents.filter {
            IDLSimpleExtendedAttributeDeclaration("NoInterfaceObject") in it.extendedAttributes
        }
        val newAttributes: MutableList<IDLAttributeDeclaration> = mutableListOf()
        val newOperations: MutableList<IDLOperationDeclaration> = mutableListOf()
        for (parent in interfaceParents) {
            for (parentOperation in parent.operations) {
                if (declaration.operations.none { it.isOverriding(parentOperation) }) {
                    newOperations += parentOperation
                }
            }
            for (parentAttribute in parent.attributes) {
                if (declaration.attributes.none { it.isOverriding(parentAttribute) }) {
                    newAttributes += parentAttribute
                }
            }
        }
        return declaration.copy(
                attributes = declaration.attributes + newAttributes,
                operations = declaration.operations + newOperations
        )
    }

    override fun lowerDictionaryDeclaration(declaration: IDLDictionaryDeclaration): IDLDictionaryDeclaration {
        return declaration
    }
}

private class OverrideContext : IDLLowering {
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

    fun resolveClass(name: String): IDLDictionaryDeclaration? = dictionaries[name]
}

fun IDLFileDeclaration.resolveOverrides(): IDLFileDeclaration {
    val context = OverrideContext()
    return OverrideResolver(context).lowerFileDeclaration(
            context.lowerFileDeclaration(this)
    )

}
