package org.jetbrains.dukat.compiler.lowerings

import org.jetbrains.dukat.ast.model.duplicate
import org.jetbrains.dukat.ast.model.nodes.ClassNode
import org.jetbrains.dukat.ast.model.nodes.DocumentRootNode
import org.jetbrains.dukat.ast.model.nodes.InterfaceNode
import org.jetbrains.dukat.ast.model.nodes.MemberNode
import org.jetbrains.dukat.ast.model.nodes.MethodNode
import org.jetbrains.dukat.ast.model.nodes.PropertyNode
import org.jetbrains.dukat.ast.model.nodes.SourceSetNode
import org.jetbrains.dukat.ast.model.nodes.TypeNode
import org.jetbrains.dukat.ast.model.nodes.UnionTypeNode
import org.jetbrains.dukat.ast.model.nodes.transform
import org.jetbrains.dukat.compiler.AstContext
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration


private fun InterfaceNode.getKnownParents(astContext: AstContext) =
        parentEntities.map { astContext.resolveInterface(it.name) }.filterNotNull()

private fun ClassNode.getKnownParents(astContext: AstContext) =
        parentEntities.flatMap { listOf(astContext.resolveInterface(it.name), astContext.resolveClass(it.name)) }.filterNotNull()

@Suppress("UNCHECKED_CAST")
private fun InterfaceNode.allParentMethods(astContext: AstContext): List<MethodNode> {
    return getKnownParents(astContext).flatMap { parentEntity ->
        parentEntity.members.filter { member -> member is MethodNode } + parentEntity.allParentMethods(astContext)
    } as List<MethodNode>
}

@Suppress("UNCHECKED_CAST")
private fun InterfaceNode.allParentProperties(astContext: AstContext): List<PropertyNode> {
    return getKnownParents(astContext).flatMap { parentEntity ->
        parentEntity.members.filter { member -> member is PropertyNode } + parentEntity.allParentProperties(astContext)
    } as List<PropertyNode>
}


@Suppress("UNCHECKED_CAST")
private fun ClassNode.allParentMethods(astContext: AstContext): List<MethodNode> {
    return getKnownParents(astContext).flatMap { parentEntity ->
        if (parentEntity is InterfaceNode) {
            parentEntity.members.filter { member -> member is MethodNode } + parentEntity.allParentMethods(astContext)
        } else if (parentEntity is ClassNode) {
            parentEntity.members.filter { member -> member is MethodNode } + parentEntity.allParentMethods(astContext)
        } else throw Exception("unkown ClassLikeDeclaration ${parentEntity}")
    } as List<MethodNode>
}

@Suppress("UNCHECKED_CAST")
private fun ClassNode.allParentProperties(astContext: AstContext): List<PropertyNode> {
    return getKnownParents(astContext).flatMap { parentEntity ->
        if (parentEntity is InterfaceNode) {
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

    if ((name == "equals") && (parameters.size == 1) && (parameters[0].type == TypeNode("Any", emptyList()))) {
        return true
    }

    if ((name == "hashCode" && parameters.isEmpty() && type == TypeNode("Number", emptyList()))) {
        return true
    }

    if ((name == "toString" && parameters.isEmpty() && type == TypeNode("String", emptyList()))) {
        return true
    }

    return false
}

private fun ParameterValueDeclaration.isOverriding(otherParameterValue: ParameterValueDeclaration): Boolean {
    //TODO: we need to do this the right way
    if (this == otherParameterValue) {
        return true
    }

    if (otherParameterValue is UnionTypeNode) {
        return otherParameterValue.params.any { isOverriding(it) }
    }

    if (otherParameterValue == TypeNode("Any", emptyList(), false, null)) {
        return true
    }

    return false
}

private fun MemberNode.lowerOverrides(
        allSuperDeclarations: List<MethodNode>,
        allSuperProperties: List<PropertyNode>
): MemberNode {

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

fun DocumentRootNode.lowerOverrides(astContext: AstContext): DocumentRootNode {
    val loweredDeclarations = declarations.map { declaration ->
        when (declaration) {
            is InterfaceNode -> {
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
            is DocumentRootNode -> {
                declaration.lowerOverrides(astContext)
            }
            else -> declaration.duplicate()
        }
    }

    return copy(declarations = loweredDeclarations)
}

private fun DocumentRootNode.updateContext(astContext: AstContext): DocumentRootNode {
    for (declaration in declarations) {
        if (declaration is InterfaceNode) {
            astContext.registerInterface(declaration)
        }
        if (declaration is ClassNode) {
            astContext.registerClass(declaration)
        }
        if (declaration is DocumentRootNode) {
            declaration.updateContext(astContext)
        }
    }

    return this
}

private fun SourceSetNode.updateContext(astContext: AstContext) = transform { it.updateContext(astContext) }


fun SourceSetNode.lowerOverrides(): SourceSetNode {
    val astContext = AstContext()
    return updateContext(astContext).transform {
        it.lowerOverrides(astContext)
    }
}