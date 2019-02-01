package org.jetbrains.dukat.compiler.lowerings

import org.jetbrains.dukat.ast.model.nodes.ClassLikeNode
import org.jetbrains.dukat.ast.model.nodes.ClassNode
import org.jetbrains.dukat.ast.model.nodes.FunctionNode
import org.jetbrains.dukat.ast.model.nodes.InterfaceNode
import org.jetbrains.dukat.ast.model.nodes.MethodNode
import org.jetbrains.dukat.ast.model.nodes.PropertyNode
import org.jetbrains.dukat.astCommon.MemberDeclaration
import org.jetbrains.dukat.compiler.AstContext
import org.jetbrains.dukat.tsmodel.DocumentRootDeclaration
import org.jetbrains.dukat.tsmodel.types.ObjectLiteralDeclaration
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration


private class GenerateInterfaceReferences(private val astContext: AstContext) : IdentityLowering {

    private fun ParameterValueDeclaration.generateInterface(owner: ClassLikeNode): ParameterValueDeclaration {
        return when (this) {
            is ObjectLiteralDeclaration -> {
                val referenceNode = astContext.registerObjectLiteralDeclaration(this, owner.generatedReferenceNodes)
                referenceNode
            }
            else -> this
        }
    }

    private fun ParameterValueDeclaration.generateInterface(owner: FunctionNode): ParameterValueDeclaration {
        return when (this) {
            is ObjectLiteralDeclaration -> {
                val referenceNode = astContext.registerObjectLiteralDeclaration(this, owner.generatedReferenceNodes)
                referenceNode
            }
            else -> this
        }
    }


    fun lowerMemberDeclaration(declaration: MemberDeclaration, owner: ClassLikeNode): MemberDeclaration {
        return when (declaration) {
            is PropertyNode -> declaration.copy(type = declaration.type.generateInterface(owner))
            is MethodNode -> declaration.copy(
                    parameters = declaration.parameters.map { param ->
                        param.copy(type = param.type.generateInterface(owner))
                    },
                    type = declaration.type.generateInterface(owner)
            )
            else -> declaration
        }
    }

    override fun lowerInterfaceNode(declaration: InterfaceNode): InterfaceNode {
        return declaration.copy(members = declaration.members.map { member -> lowerMemberDeclaration(member, declaration) })
    }

    override fun lowerClassNode(declaration: ClassNode): ClassNode {
        return declaration.copy(members = declaration.members.map { member -> lowerMemberDeclaration(member, declaration) })
    }

    override fun lowerFunctionNode(declaration: FunctionNode): FunctionNode {
        return declaration.copy(parameters = declaration.parameters.map { param ->
            param.copy(type = param.type.generateInterface(declaration))
        })
    }
}

fun DocumentRootDeclaration.generateInterfaceReferences(astContext: AstContext): DocumentRootDeclaration {
    return GenerateInterfaceReferences(astContext).lowerDocumentRoot(this)
}