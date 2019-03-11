package org.jetbrains.dukat.compiler.lowerings

import org.jetbrains.dukat.ast.model.nodes.ClassNode
import org.jetbrains.dukat.ast.model.nodes.DocumentRootNode
import org.jetbrains.dukat.ast.model.nodes.FunctionNode
import org.jetbrains.dukat.ast.model.nodes.InterfaceNode
import org.jetbrains.dukat.ast.model.nodes.MemberNode
import org.jetbrains.dukat.ast.model.nodes.MethodNode
import org.jetbrains.dukat.ast.model.nodes.SourceSetNode
import org.jetbrains.dukat.ast.model.nodes.transform
import org.jetbrains.dukat.tsmodel.ParameterDeclaration

private data class MethodNodeKey(val name:String, val params: List<ParameterDeclaration>)

private class IntroduceMissedOverloads : ParameterValueLowering {

    private fun MethodNode.resolveMissedOverloads(resolvedMembers: MutableSet<MethodNodeKey>) : MemberNode? {

        val nonOptionalHead = parameters.takeWhile {
            !it.optional || (it.initializer != null)
        }

        return if (nonOptionalHead.size != parameters.size) {
            val missedMethodNode = copy(name = name, parameters = nonOptionalHead)
            val missedMethodKey = MethodNodeKey(
                    missedMethodNode.name,
                    missedMethodNode.parameters.map { it.copy(name = "x")}
            )
            if (resolvedMembers.contains(missedMethodKey)) {
                null
            } else {
                resolvedMembers.add(missedMethodKey)
                missedMethodNode
            }
        } else {
            null
        }
    }

    private fun FunctionNode.resolveMissedOverloads(resolvedMembers: MutableSet<MethodNodeKey>) : FunctionNode? {
        val nonOptionalHead = parameters.takeWhile {
            !it.optional || (it.initializer != null)
        }
        return if (nonOptionalHead.size != parameters.size) {
            val missedMethodNode = copy(name = name, parameters = nonOptionalHead)
            val missedMethodKey = MethodNodeKey(
                    missedMethodNode.name,
                    missedMethodNode.parameters.map { it.copy(name = "x")}
            )
            if (resolvedMembers.contains(missedMethodKey)) {
                null
            } else {
                resolvedMembers.add(missedMethodKey)
                missedMethodNode
            }
        } else {
            null
        }
    }


    override fun lowerInterfaceNode(declaration: InterfaceNode): InterfaceNode {
        val resolvedMembers = mutableSetOf<MethodNodeKey>()

        val membersWithMissedOverloads = declaration.members.mapNotNull { member ->
            if (member is MethodNode) {
                member.resolveMissedOverloads(resolvedMembers)
            } else {
                null
            }
        }

        return declaration.copy(members = declaration.members + membersWithMissedOverloads)
    }

    override fun lowerClassNode(declaration: ClassNode): ClassNode {
        val resolvedMembers = mutableSetOf<MethodNodeKey>()

        val membersWithMissedOverloads = declaration.members.mapNotNull { member ->
            if (member is MethodNode) {
                member.resolveMissedOverloads(resolvedMembers)
            } else {
                null
            }
        }

        return declaration.copy(members = declaration.members + membersWithMissedOverloads)
    }

    override fun lowerDocumentRoot(documentRoot: DocumentRootNode): DocumentRootNode {
        val resolvedMembers = mutableSetOf<MethodNodeKey>()

        val membersWithMissedOverloads = documentRoot.declarations.mapNotNull { member ->
            if (member is FunctionNode) {
                member.resolveMissedOverloads(resolvedMembers)
            } else {
                null
            }
        }

        return documentRoot.copy(declarations = documentRoot.declarations.map { lowerTopLevelDeclaration(it) } + membersWithMissedOverloads)
    }
}

fun DocumentRootNode.introduceMissedOverloads() : DocumentRootNode {
    return IntroduceMissedOverloads().lowerDocumentRoot(this)
}

fun SourceSetNode.introduceMissedOverloads() = transform { it.introduceMissedOverloads() }