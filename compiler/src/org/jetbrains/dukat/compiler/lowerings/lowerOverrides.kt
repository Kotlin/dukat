package org.jetbrains.dukat.compiler.lowerings

import org.jetbrains.dukat.ast.AstContext
import org.jetbrains.dukat.ast.model.duplicate
import org.jetbrains.dukat.ast.model.nodes.ClassNode
import org.jetbrains.dukat.ast.model.nodes.MethodNode
import org.jetbrains.dukat.ast.model.nodes.PropertyNode
import org.jetbrains.dukat.tsmodel.DocumentRootDeclaration
import org.jetbrains.dukat.tsmodel.InterfaceDeclaration
import org.jetbrains.dukat.tsmodel.MemberDeclaration
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration
import org.jetbrains.dukat.tsmodel.types.TypeDeclaration


private fun InterfaceDeclaration.getKnownParents(astContext: AstContext) =
        parentEntities.map { astContext.resolveInterface(it.name) }.filterNotNull()

private fun ClassNode.getKnownParents(astContext: AstContext) =
        parentEntities.flatMap { listOf(astContext.resolveInterface(it.name), astContext.resolveClass(it.name)) }.filterNotNull()

@Suppress("UNCHECKED_CAST")
private fun InterfaceDeclaration.allParentMethods(astContext: AstContext): List<MethodNode> {
    return getKnownParents(astContext).flatMap { parentEntity ->
        parentEntity.members.filter { member -> member is MethodNode } + parentEntity.allParentMethods(astContext)
    } as List<MethodNode>
}

@Suppress("UNCHECKED_CAST")
private fun InterfaceDeclaration.allParentProperties(astContext: AstContext): List<PropertyNode> {
    return getKnownParents(astContext).flatMap { parentEntity ->
        parentEntity.members.filter { member -> member is PropertyNode } + parentEntity.allParentProperties(astContext)
    } as List<PropertyNode>
}


@Suppress("UNCHECKED_CAST")
private fun ClassNode.allParentMethods(astContext: AstContext): List<MethodNode> {
    return getKnownParents(astContext).flatMap { parentEntity ->
        if (parentEntity is InterfaceDeclaration) {
            parentEntity.members.filter { member -> member is MethodNode } + parentEntity.allParentMethods(astContext)
        } else if (parentEntity is ClassNode) {
            parentEntity.members.filter { member -> member is MethodNode } + parentEntity.allParentMethods(astContext)
        } else throw Exception("unkown ClassLikeDeclaration ${parentEntity}")
    } as List<MethodNode>
}

@Suppress("UNCHECKED_CAST")
private fun ClassNode.allParentProperties(astContext: AstContext): List<PropertyNode> {
    return getKnownParents(astContext).flatMap { parentEntity ->
        if (parentEntity is InterfaceDeclaration) {
            parentEntity.members.filter { member -> member is PropertyNode } + parentEntity.allParentProperties(astContext)
        } else if (parentEntity is ClassNode) {
            parentEntity.members.filter { member -> member is PropertyNode } + parentEntity.allParentProperties(astContext)
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
            .zip(otherMethodDeclaration.parameters) { a, b -> a.type.isOverriding(b.type) }
            .all { it }
}

private fun PropertyNode.isOverriding(otherPropertyDeclaration: PropertyNode): Boolean {
    return type.isOverriding(otherPropertyDeclaration.type)
}


private fun MethodNode.isSpecialCase(): Boolean {

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

    if (otherParameterValue == TypeDeclaration("Any", emptyList(), false, null)) {
        return true
    }

    return false
}

private fun MemberDeclaration.lowerOverrides(
        allSuperDeclarations: List<MethodNode>,
        allSuperProperties: List<PropertyNode>
): MemberDeclaration {

    return if (this is MethodNode) {
        val override =
                allSuperDeclarations.any { superMethod -> isOverriding(superMethod) } || isSpecialCase()
        copy(override = override)
    } else if (this is PropertyNode) {
        val override = allSuperProperties.any { superMethod ->
            isOverriding(superMethod)
        }
        copy(override = override)
    } else this
}

fun DocumentRootDeclaration.lowerOverrides(astContext: AstContext): DocumentRootDeclaration {
    val loweredDeclarations = declarations.map { declaration ->
        when (declaration) {
            is InterfaceDeclaration -> {
                val allParentMethods = declaration.allParentMethods(astContext)
                val allParentProperties = declaration.allParentProperties(astContext)

                declaration.copy(
                        members = declaration.members.map { member ->
                            member.lowerOverrides(allParentMethods, allParentProperties)
                        }
                )
            }
            is ClassNode -> {
                val allParentMethods = declaration.allParentMethods(astContext)
                val allParentProperties = declaration.allParentProperties(astContext)

                declaration.copy(
                        members = declaration.members.map { member ->
                            member.lowerOverrides(allParentMethods, allParentProperties)
                        }
                )
            }
            is DocumentRootDeclaration -> {
                declaration.lowerOverrides(astContext)
            }
            else -> declaration.duplicate()
        }
    }

    return copy(declarations = loweredDeclarations)
}