package org.jetbrains.dukat.idlLowerings

import org.jetbrains.dukat.idlDeclarations.IDLAttributeDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLDictionaryDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLDictionaryMemberDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLFileDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLGetterDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLInterfaceDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLOperationDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLSetterDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLSingleTypeDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLTypeDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLUnionTypeDeclaration
import org.jetbrains.dukat.idlDeclarations.changeComment


internal class OverrideHelper(val context: MissingMemberContext) {
    private fun getFirstLevelParents(declaration: IDLInterfaceDeclaration): List<IDLInterfaceDeclaration> {
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

    private fun isOverriding(type: IDLTypeDeclaration, parentType: IDLTypeDeclaration): Boolean {
        if (type.changeComment(null) == parentType.changeComment(null)) {
            return true
        }

        if (parentType is IDLSingleTypeDeclaration && parentType.name == "any") {
            return true
        }

        if (type is IDLSingleTypeDeclaration && parentType is IDLSingleTypeDeclaration) {
            val classLike = context.resolveInterface(type.name)
                    ?: context.resolveDictionary(type.name)
            val parentClassLike = context.resolveInterface(parentType.name)
                    ?: context.resolveDictionary(parentType.name)
            return when (classLike) {
                is IDLInterfaceDeclaration -> parentClassLike in getAllInterfaceParents(classLike)
                is IDLDictionaryDeclaration -> parentClassLike in getAllDictionaryParents(classLike)
                else -> false
            }
        }

        if (parentType is IDLUnionTypeDeclaration) {
            return parentType.unionMembers.any { isOverriding(type, it) }
        }

        return false
    }

    fun isOverriding(member: IDLDictionaryMemberDeclaration, parentMember: IDLDictionaryMemberDeclaration): Boolean {
        return member.name == parentMember.name
    }

    fun isOverriding(member: IDLAttributeDeclaration, parentMember: IDLAttributeDeclaration): Boolean {
        return member.name == parentMember.name &&
                member.static == parentMember.static &&
                isOverriding(member.type, parentMember.type) &&
                !(member.readOnly && !parentMember.readOnly)
    }

    fun isConflicting(member: IDLAttributeDeclaration, parentMember: IDLAttributeDeclaration): Boolean {
        return member.name == parentMember.name && !isOverriding(member, parentMember)
    }

    fun isOverriding(member: IDLOperationDeclaration, parentMember: IDLOperationDeclaration): Boolean {
        return member.name == parentMember.name &&
                member.static == parentMember.static &&
                member.arguments.size == parentMember.arguments.size &&
                (member.arguments.isEmpty() ||
                        member.arguments.zip(parentMember.arguments) { a, b -> isOverriding(a.type, b.type) }.all { it })
    }

    fun isConflicting(member: IDLOperationDeclaration, parentMember: IDLOperationDeclaration): Boolean {
        return isOverriding(member, parentMember) && !isOverriding(member.returnType, parentMember.returnType)
    }

    fun isOverriding(member: IDLGetterDeclaration, parentMember: IDLGetterDeclaration): Boolean {
        return member.name == parentMember.name &&
                isOverriding(member.valueType, parentMember.valueType) &&
                isOverriding(member.key.type, parentMember.key.type)
    }

    fun isOverriding(member: IDLSetterDeclaration, parentMember: IDLSetterDeclaration): Boolean {
        return member.name == parentMember.name &&
                isOverriding(member.key.type, parentMember.key.type) &&
                isOverriding(member.value.type, parentMember.value.type)
    }

    fun isSimilar(member: IDLAttributeDeclaration, parentMember: IDLAttributeDeclaration): Boolean {
        return member.name == parentMember.name &&
                member.type == parentMember.type &&
                member.readOnly == parentMember.readOnly &&
                member.override == parentMember.override
    }

    fun isSimilar(member: IDLOperationDeclaration, parentMember: IDLOperationDeclaration): Boolean {
        return member.name == parentMember.name &&
                member.returnType == parentMember.returnType &&
                member.arguments.size == parentMember.arguments.size &&
                member.arguments.zip(parentMember.arguments).all { arguments ->
                    arguments.first.type == arguments.second.type
                } &&
                member.override == parentMember.override
    }

}

internal class MissingMemberContext : IDLLowering {
    private val interfaces: MutableMap<String, IDLInterfaceDeclaration> = mutableMapOf()
    private val dictionaries: MutableMap<String, IDLDictionaryDeclaration> = mutableMapOf()

    override fun lowerInterfaceDeclaration(declaration: IDLInterfaceDeclaration, owner: IDLFileDeclaration): IDLInterfaceDeclaration {
        interfaces[declaration.name] = declaration
        return declaration
    }

    override fun lowerDictionaryDeclaration(declaration: IDLDictionaryDeclaration, owner: IDLFileDeclaration): IDLDictionaryDeclaration {
        dictionaries[declaration.name] = declaration
        return declaration
    }

    fun resolveInterface(name: String): IDLInterfaceDeclaration? = interfaces[name]

    fun resolveDictionary(name: String): IDLDictionaryDeclaration? = dictionaries[name]
}
