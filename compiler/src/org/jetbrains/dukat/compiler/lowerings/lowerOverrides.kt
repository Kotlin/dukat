package org.jetbrains.dukat.compiler.lowerings

import org.jetbrains.dukat.ast.model.ClassDeclaration
import org.jetbrains.dukat.ast.model.Declaration
import org.jetbrains.dukat.ast.model.DocumentRoot
import org.jetbrains.dukat.ast.model.InterfaceDeclaration
import org.jetbrains.dukat.ast.model.MemberDeclaration
import org.jetbrains.dukat.ast.model.MethodDeclaration
import org.jetbrains.dukat.ast.model.ParameterValue
import org.jetbrains.dukat.ast.model.PropertyDeclaration
import org.jetbrains.dukat.ast.model.TypeDeclaration
import org.jetbrains.dukat.ast.model.duplicate

@Suppress("UNCHECKED_CAST")
private fun InterfaceDeclaration.allParentMethods() : List<MethodDeclaration> {
    return parentEntities.flatMap { parentEntity ->
        parentEntity.members.filter {member -> member is MethodDeclaration} + parentEntity.allParentMethods()
    } as List<MethodDeclaration>
}

@Suppress("UNCHECKED_CAST")
private fun ClassDeclaration.allParentMethods() : List<MethodDeclaration> {
    return parentEntities.flatMap { parentEntity ->
        if (parentEntity is InterfaceDeclaration) {
            parentEntity.members.filter {member -> member is MethodDeclaration} + parentEntity.allParentMethods()
        } else if (parentEntity is ClassDeclaration) {
            parentEntity.members.filter {member -> member is MethodDeclaration} + parentEntity.allParentMethods()
        } else throw Exception("unkown ClassLikeDeclaration ${parentEntity}")
    } as List<MethodDeclaration>
}

@Suppress("UNCHECKED_CAST")
private fun InterfaceDeclaration.allParentProperties() : List<PropertyDeclaration> {
    return parentEntities.flatMap { parentEntity ->
        parentEntity.members.filter {member -> member is PropertyDeclaration} + parentEntity.allParentProperties()
    } as List<PropertyDeclaration>
}

@Suppress("UNCHECKED_CAST")
private fun ClassDeclaration.allParentProperties() : List<PropertyDeclaration> {
    return parentEntities.flatMap { parentEntity ->
        if (parentEntity is InterfaceDeclaration) {
            parentEntity.members.filter {member -> member is PropertyDeclaration} + parentEntity.allParentProperties()
        } else if (parentEntity is ClassDeclaration) {
            parentEntity.members.filter {member -> member is PropertyDeclaration} + parentEntity.allParentProperties()
        } else throw Exception("unkown ClassLikeDeclaration ${parentEntity}")
    } as List<PropertyDeclaration>
}

private fun MethodDeclaration.isOverriding(otherMethodDeclaration: MethodDeclaration): Boolean {
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

private fun PropertyDeclaration.isOverriding(otherPropertyDeclaration: PropertyDeclaration) : Boolean {
    return type.isOverriding(otherPropertyDeclaration.type)
}


private fun MethodDeclaration.isSpecialCase() : Boolean {

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

private fun ParameterValue.isOverriding(otherParameterValue: ParameterValue): Boolean {
    //TODO: we need to do this the right way
    if (this == otherParameterValue) {
        return true
    }

    if (otherParameterValue == TypeDeclaration("Any", emptyArray())) {
        return true
    }

    return false
}

private fun MemberDeclaration.lowerOverrides(
        allSuperDeclarations: List<MethodDeclaration>,
        allSuperProperties: List<PropertyDeclaration>
) : MemberDeclaration {
    if (this is MethodDeclaration) {
        val override =
                allSuperDeclarations.any { superMethod -> isOverriding(superMethod) } || isSpecialCase()
        return copy(override = override)
    } else if (this is PropertyDeclaration) {
        val override =  allSuperProperties.any { superMethod -> isOverriding(superMethod) }
        return copy(override = override)
    } else {
        throw Exception("can not lower overrides for ${this}")
    }
}

fun DocumentRoot.lowerOverrides(): DocumentRoot {
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
            else -> declaration.duplicate<Declaration>()
        }
    }

    return copy(declarations = loweredDeclarations)
}