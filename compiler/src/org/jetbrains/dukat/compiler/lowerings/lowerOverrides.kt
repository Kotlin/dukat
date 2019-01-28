package org.jetbrains.dukat.compiler.lowerings

import org.jetbrains.dukat.ast.model.declaration.ClassDeclaration
import org.jetbrains.dukat.ast.model.declaration.Declaration
import org.jetbrains.dukat.ast.model.declaration.DocumentRootDeclaration
import org.jetbrains.dukat.ast.model.declaration.InterfaceDeclaration
import org.jetbrains.dukat.ast.model.declaration.MemberDeclaration
import org.jetbrains.dukat.ast.model.declaration.types.ParameterValueDeclaration
import org.jetbrains.dukat.ast.model.declaration.types.TypeDeclaration
import org.jetbrains.dukat.ast.model.duplicate
import org.jetbrains.dukat.ast.model.nodes.MethodNode
import org.jetbrains.dukat.ast.model.nodes.PropertyNode

@Suppress("UNCHECKED_CAST")
private fun InterfaceDeclaration.allParentMethods() : List<MethodNode> {
    return parentEntities.flatMap { parentEntity ->
        parentEntity.members.filter {member -> member is MethodNode } + parentEntity.allParentMethods()
    } as List<MethodNode>
}

@Suppress("UNCHECKED_CAST")
private fun ClassDeclaration.allParentMethods() : List<MethodNode> {
    return parentEntities.flatMap { parentEntity ->
        if (parentEntity is InterfaceDeclaration) {
            parentEntity.members.filter {member -> member is MethodNode } + parentEntity.allParentMethods()
        } else if (parentEntity is ClassDeclaration) {
            parentEntity.members.filter {member -> member is MethodNode } + parentEntity.allParentMethods()
        } else throw Exception("unkown ClassLikeDeclaration ${parentEntity}")
    } as List<MethodNode>
}

@Suppress("UNCHECKED_CAST")
private fun InterfaceDeclaration.allParentProperties() : List<PropertyNode> {
    return parentEntities.flatMap { parentEntity ->
        parentEntity.members.filter {member -> member is PropertyNode } + parentEntity.allParentProperties()
    } as List<PropertyNode>
}

@Suppress("UNCHECKED_CAST")
private fun ClassDeclaration.allParentProperties() : List<PropertyNode> {
    return parentEntities.flatMap { parentEntity ->
        if (parentEntity is InterfaceDeclaration) {
            parentEntity.members.filter {member -> member is PropertyNode } + parentEntity.allParentProperties()
        } else if (parentEntity is ClassDeclaration) {
            parentEntity.members.filter {member -> member is PropertyNode } + parentEntity.allParentProperties()
        } else throw Exception("unkown ClassLikeDeclaration ${parentEntity}")
    } as List<PropertyNode>
}

private fun MethodNode.isOverriding(otherMethodDeclaration: MethodNode): Boolean {
    if (name != otherMethodDeclaration.name) {
        return false
    }

    if (parameters.size != otherMethodDeclaration.parameters.size) {
        return false
    }

    if (typeParameters.size != otherMethodDeclaration.typeParameters.size) {
        return false
    }

    return parameters
            .zip(otherMethodDeclaration.parameters) {a, b -> a.type.isOverriding(b.type)}
            .all { it }
}

private fun PropertyNode.isOverriding(otherPropertyDeclaration: PropertyNode) : Boolean {
    return type.isOverriding(otherPropertyDeclaration.type)
}


private fun MethodNode.isSpecialCase() : Boolean {

    if ((name == "equals") && (parameters.size == 1) && (parameters[0].type == TypeDeclaration("Any", emptyArray()))) {
        return true
    }

    if ((name == "hashCode" && parameters.isEmpty() && type == TypeDeclaration("Number", emptyArray()))) {
        return true
    }

    if ((name == "toString" && parameters.isEmpty() && type == TypeDeclaration("String", emptyArray()))) {
        return true
    }

    return false
}

private fun ParameterValueDeclaration.isOverriding(otherParameterValue: ParameterValueDeclaration): Boolean {
    //TODO: we need to do this the right way
    if (this == otherParameterValue) {
        return true
    }

    if (otherParameterValue == TypeDeclaration("Any", emptyList(), false, false, null)) {
        return true
    }

    return false
}

private fun MemberDeclaration.lowerOverrides(
        allSuperDeclarations: List<MethodNode>,
        allSuperProperties: List<PropertyNode>
) : MemberDeclaration {

    return if (this is MethodNode) {
        val override =
                allSuperDeclarations.any { superMethod -> isOverriding(superMethod) } || isSpecialCase()
         copy(override = override)
    } else if (this is PropertyNode) {
        val override =  allSuperProperties.any {
            superMethod ->
            isOverriding(superMethod)
        }
        copy(override = override)
    } else this
}

fun DocumentRootDeclaration.lowerOverrides(): DocumentRootDeclaration {
    val loweredDeclarations = declarations.map { declaration ->
        when (declaration) {
            is InterfaceDeclaration -> {
                val allParentMethods = declaration.allParentMethods()
                val allParentProperties = declaration.allParentProperties()

                declaration.copy(
                        members = declaration.members.map {member ->
                            member.lowerOverrides(allParentMethods, allParentProperties)}
                )
            }
            is ClassDeclaration -> {
                val allParentMethods = declaration.allParentMethods()
                val allParentProperties = declaration.allParentProperties()

                declaration.copy(
                        members = declaration.members.map {member ->
                            member.lowerOverrides(allParentMethods, allParentProperties)}
                )
            }
            is DocumentRootDeclaration -> {
                declaration.lowerOverrides()
            }
            else -> declaration.duplicate<Declaration>()
        }
    }

    return copy(declarations = loweredDeclarations)
}