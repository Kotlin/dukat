package org.jetbrains.dukat.compiler.lowerings

import org.jetbrains.dukat.ast.model.duplicate
import org.jetbrains.dukat.ast.model.nodes.ClassLikeNode
import org.jetbrains.dukat.ast.model.nodes.ClassNode
import org.jetbrains.dukat.ast.model.nodes.DocumentRootNode
import org.jetbrains.dukat.ast.model.nodes.InterfaceNode
import org.jetbrains.dukat.ast.model.nodes.MemberNode
import org.jetbrains.dukat.ast.model.nodes.MethodNode
import org.jetbrains.dukat.ast.model.nodes.PropertyNode
import org.jetbrains.dukat.ast.model.nodes.SourceSetNode
import org.jetbrains.dukat.ast.model.nodes.TypeValueNode
import org.jetbrains.dukat.ast.model.nodes.UnionTypeNode
import org.jetbrains.dukat.ast.model.nodes.transform
import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.compiler.AstContext
import org.jetbrains.dukat.panic.raiseConcern
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration


private fun ClassLikeNode.translate(): String {
    return when (this) {
        is ClassNode -> "<${name}:${uid}>"
        is InterfaceNode -> "<${name}:${uid}>"
        else -> "<UNKNOWN>"
    }
}

private class OverrideResolver(private val astContext: AstContext) {
    private fun ClassLikeNode.getKnownParents(): List<ClassLikeNode> {
        return when (this) {
            is InterfaceNode -> getKnownParents()
            is ClassNode -> getKnownParents()
            else -> raiseConcern("unknown ClassLikeDeclaration ${this::class.simpleName}") { emptyList<ClassLikeNode>() }
        }
    }

    private fun InterfaceNode.getKnownParents(): List<InterfaceNode> {
        return parentEntities.flatMap { heritageNode ->
            val interfaceNode = astContext.resolveInterface(heritageNode.name)

            if (interfaceNode == null) {
                emptyList()
            } else {
                listOf(interfaceNode) + interfaceNode.getKnownParents()
            }
        }
    }


    private fun ClassNode.getKnownParents(): List<ClassLikeNode> {
        return parentEntities.flatMap {
            listOf<ClassLikeNode?>(astContext.resolveInterface(it.name), astContext.resolveClass(it.name))
        }.filterNotNull()
    }

    private fun InterfaceNode.allParentMethods(): List<MethodNode> {
        return getKnownParents().flatMap { parentEntity ->
            parentEntity.members.filterIsInstance(MethodNode::class.java) + parentEntity.allParentMethods()
        }
    }

    private fun InterfaceNode.allParentProperties(): List<PropertyNode> {
        return getKnownParents().flatMap { parentEntity ->
            parentEntity.members.filterIsInstance(PropertyNode::class.java) + parentEntity.allParentProperties()
        }
    }


    private fun ClassNode.allParentMethods(): List<MethodNode> {
        return getKnownParents().flatMap { parentEntity ->
            when (parentEntity) {
                is InterfaceNode -> parentEntity.members.filterIsInstance(MethodNode::class.java) + parentEntity.allParentMethods()
                is ClassNode -> parentEntity.members.filterIsInstance(MethodNode::class.java) + parentEntity.allParentMethods()
                else -> raiseConcern("unknown ClassLikeDeclaration ${parentEntity}") { emptyList<MethodNode>() }
            }
        }
    }

    private fun ClassNode.allParentProperties(): List<PropertyNode> {
        return getKnownParents().flatMap { parentEntity ->
            when (parentEntity) {
                is InterfaceNode -> parentEntity.members.filterIsInstance(PropertyNode::class.java) + parentEntity.allParentProperties()
                is ClassNode -> parentEntity.members.filterIsInstance(PropertyNode::class.java) + parentEntity.allParentProperties()
                else -> raiseConcern("unknown ClassLikeDeclaration ${parentEntity}") { emptyList<PropertyNode>() }
            }
        }
    }

    private fun MethodNode.isOverriding(otherMethodNode: MethodNode): Boolean {
        if (name != otherMethodNode.name) {
            return false
        }

        if (parameters.size != otherMethodNode.parameters.size) {
            return false
        }

        if (typeParameters.size != otherMethodNode.typeParameters.size) {
            return false
        }

        return parameters
                .zip(otherMethodNode.parameters) { a, b -> a.type.isOverriding(b.type) }
                .all { it }
    }

    private fun PropertyNode.isOverriding(otherPropertyNode: PropertyNode): Boolean {
        return (name == otherPropertyNode.name) && type.isOverriding(otherPropertyNode.type)
    }


    private fun MethodNode.isSpecialCase(): Boolean {

        if ((name == "equals") && (parameters.size == 1) && (parameters[0].type == TypeValueNode(IdentifierEntity("Any"), emptyList()))) {
            return true
        }

        if ((name == "hashCode" && parameters.isEmpty() && type == TypeValueNode(IdentifierEntity("Number"), emptyList()))) {
            return true
        }

        if ((name == "toString" && parameters.isEmpty() && type == TypeValueNode(IdentifierEntity("String"), emptyList()))) {
            return true
        }

        return false
    }

    private fun ParameterValueDeclaration.isOverriding(otherParameterValue: ParameterValueDeclaration): Boolean {
        //TODO: we need to do this the right way
        if (this == otherParameterValue) {
            return true
        }

        if ((this is TypeValueNode) && (otherParameterValue is TypeValueNode)) {

            val classLike: ClassLikeNode? = astContext.resolveClass(value) ?: astContext.resolveInterface(value)
            val otherClassLike: ClassLikeNode? = astContext.resolveClass(otherParameterValue.value)
                    ?: astContext.resolveInterface(otherParameterValue.value)


            if (classLike != null) {
                return classLike.getKnownParents().contains(otherClassLike)
            }
        }

        if (otherParameterValue is UnionTypeNode) {
            return otherParameterValue.params.any { isOverriding(it) }
        }

        if (otherParameterValue == TypeValueNode(IdentifierEntity("Any"), emptyList(), false, null)) {
            return true
        }

        return false
    }

    private fun MemberNode.lowerOverrides(
            allSuperDeclarations: List<MethodNode>,
            allSuperProperties: List<PropertyNode>
    ): MemberNode {

        return when (this) {
            is MethodNode -> {
                val override =
                        allSuperDeclarations.any { superMethod -> isOverriding(superMethod) } || isSpecialCase()
                copy(override = override)
            }
            is PropertyNode -> {
                val override = allSuperProperties.any { superMethod ->
                    isOverriding(superMethod)
                }
                copy(override = override)
            }
            else -> this
        }
    }

    fun lowerOverrides(documentRootNode: DocumentRootNode): DocumentRootNode {
        val loweredDeclarations = documentRootNode.declarations.map { declaration ->
            when (declaration) {
                is InterfaceNode -> {
                    val allParentMethods = declaration.allParentMethods()
                    val allParentProperties = declaration.allParentProperties()

                    declaration.copy(
                            members = declaration.members.map { member ->
                                member.lowerOverrides(allParentMethods, allParentProperties)
                            }
                    )
                }
                is ClassNode -> {
                    val allParentMethods = declaration.allParentMethods()
                    val allParentProperties = declaration.allParentProperties()

                    declaration.copy(
                            members = declaration.members.map { member ->
                                member.lowerOverrides(allParentMethods, allParentProperties)
                            }
                    )
                }
                is DocumentRootNode -> {
                    lowerOverrides(declaration)
                }
                else -> declaration.duplicate()
            }
        }

        return documentRootNode.copy(declarations = loweredDeclarations)
    }

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
    val overrideResolver = OverrideResolver(astContext)
    return updateContext(astContext).transform {
        overrideResolver.lowerOverrides(it)
    }
}