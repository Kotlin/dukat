package org.jetbrains.dukat.compiler.lowerings.merge

import org.jetbrains.dukat.ast.model.nodes.HeritageNode
import org.jetbrains.dukat.ast.model.nodes.IdentifierNode
import org.jetbrains.dukat.ast.model.nodes.NameNode
import org.jetbrains.dukat.ast.model.nodes.QualifiedNode
import org.jetbrains.dukat.ast.model.nodes.TypeNode
import org.jetbrains.dukat.ast.model.nodes.appendRight
import org.jetbrains.dukat.ast.model.nodes.debugTranslate
import org.jetbrains.dukat.ast.model.nodes.shiftRight
import org.jetbrains.dukat.ast.model.nodes.size
import org.jetbrains.dukat.ast.model.nodes.translate
import org.jetbrains.dukat.astModel.ModuleModel
import org.jetbrains.dukat.astModel.SourceSetModel
import org.jetbrains.dukat.astModel.TypeValueModel
import org.jetbrains.dukat.astModel.transform
import org.jetbrains.dukat.compiler.lowerings.model.ModelWithOwnerTypeLowering
import org.jetbrains.dukat.ownerContext.NodeOwner

private fun NameNode.shiftLeft(): NameNode {
    if (this is QualifiedNode) {
        return when (left) {
            is IdentifierNode -> right
            is QualifiedNode -> QualifiedNode((left as QualifiedNode).right, right)
            else -> throw Exception("unknown ")
        }
    }

    return this
}

private class SpecifyTypeNodes(private val declarationResolver: DeclarationResolver) : ModelWithOwnerTypeLowering {

    override fun lowerTypeNode(ownerContext: NodeOwner<TypeNode>): TypeNode {
        val declaration = ownerContext.node
        val qualifiedName = ownerContext.getQualifiedName()

        if (declaration is TypeValueModel) {
            val declarationValue = declaration.value

            if (declarationValue is IdentifierNode) {
                declarationResolver.resolve(declarationValue.value, qualifiedName)?.let { declarationOwnerContext ->
                    val declarationQualifiedName = declarationOwnerContext.getQualifiedName()

                    if (declarationQualifiedName.size > 1) {
                        if (declarationQualifiedName != qualifiedName) {
                            val qualifiedNode = declarationValue.appendRight(declarationQualifiedName).shiftLeft()
                            return TypeValueModel(IdentifierNode(qualifiedNode.translate()), emptyList(), null)
                        }
                    }
                }
            }
        } else if (declaration is QualifiedNode) {
            val qualifiedPath = qualifiedName.appendRight(declaration).shiftRight()

            declarationResolver.resolveStrict(declaration.right.value, qualifiedPath)?.let { declarationOwnerContext ->
                val declarationQualifiedName = declarationOwnerContext.getQualifiedName()

                if (declarationQualifiedName != qualifiedName) {
                    val qualifiedNode = qualifiedPath?.shiftLeft()?.appendRight(declaration.right)
                    println("substituting ${declaration.debugTranslate()} with ${qualifiedNode?.translate()} ()")
                    return TypeValueModel(IdentifierNode(qualifiedNode!!.translate()), emptyList(), null)
                }
            }

        }
        return super.lowerTypeNode(ownerContext)
    }

    override fun lowerHeritageNode(ownerContext: NodeOwner<HeritageNode>): HeritageNode {
        val heritageClause = ownerContext.node
        val name = heritageClause.name

        if (name is IdentifierNode) {
            declarationResolver.resolve(name.value, ownerContext.getQualifiedName())?.let { declarationOwnerContext ->
                val declarationQualifiedName = declarationOwnerContext.getQualifiedName()
                if (declarationQualifiedName != ownerContext.getQualifiedName()) {
                    // TODO: use QualifierNode instead of IdentifierNode
                    val qualifiedNode = name.appendRight(declarationQualifiedName.shiftLeft())
                    return heritageClause.copy(name = IdentifierNode(qualifiedNode.debugTranslate()))
                }
            }
        }

        return super.lowerHeritageNode(ownerContext)
    }
}

fun ModuleModel.specifyTypeNodesWithModuleData(): ModuleModel {

    val declarationContext = DeclarationResolver()
    declarationContext.process(this, NodeOwner(this, null))

    return SpecifyTypeNodes(declarationContext).lowerRoot(this, NodeOwner(this, null))
}

fun SourceSetModel.specifyTypeNodesWithModuleData() = transform { it.specifyTypeNodesWithModuleData() }