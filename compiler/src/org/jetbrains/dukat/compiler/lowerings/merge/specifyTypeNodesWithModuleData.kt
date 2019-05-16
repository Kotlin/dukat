package org.jetbrains.dukat.compiler.lowerings.merge

import org.jetbrains.dukat.ast.model.nodes.GenericIdentifierNode
import org.jetbrains.dukat.ast.model.nodes.IdentifierNode
import org.jetbrains.dukat.ast.model.nodes.QualifiedNode
import org.jetbrains.dukat.ast.model.nodes.processing.appendRight
import org.jetbrains.dukat.ast.model.nodes.processing.debugTranslate
import org.jetbrains.dukat.ast.model.nodes.processing.process
import org.jetbrains.dukat.ast.model.nodes.processing.shiftRight
import org.jetbrains.dukat.ast.model.nodes.processing.size
import org.jetbrains.dukat.ast.model.nodes.processing.translate
import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astModel.HeritageModel
import org.jetbrains.dukat.astModel.ModuleModel
import org.jetbrains.dukat.astModel.SourceSetModel
import org.jetbrains.dukat.astModel.TypeModel
import org.jetbrains.dukat.astModel.TypeValueModel
import org.jetbrains.dukat.astModel.VariableModel
import org.jetbrains.dukat.astModel.transform
import org.jetbrains.dukat.compiler.lowerings.model.ModelWithOwnerTypeLowering
import org.jetbrains.dukat.ownerContext.NodeOwner
import org.jetbrains.dukat.panic.raiseConcern

private fun NameEntity.shiftLeft(): NameEntity {
    if (this is QualifiedNode) {
        return when (left) {
            is IdentifierNode -> right
            is GenericIdentifierNode -> right
            is QualifiedNode -> QualifiedNode((left as QualifiedNode).right, right)
            else -> raiseConcern("unknown org.jetbrains.dukat.astCommon.NameEntity") { this }
        }
    }

    return this
}

private fun NameEntity?.matchesLeft(identifier: NameEntity): Boolean {
    return when (this) {
        is IdentifierNode -> identifier == this
        is QualifiedNode -> left.matchesLeft(identifier)
        else -> false
    }
}


private fun unescape(name: String): String {
    return name.replace("(?:^`)|(?:`$)".toRegex(), "")
}


private class SpecifyTypeNodes(private val declarationResolver: DeclarationResolver) : ModelWithOwnerTypeLowering {

    override fun lowerTypeNode(ownerContext: NodeOwner<TypeModel>): TypeModel {
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
                            return TypeValueModel(qualifiedNode, emptyList(), null)
                        }
                    }
                }
            } else if (declarationValue is QualifiedNode) {

                // TODO: Double check deeply nested qualifiedNames
                val qualifiedPath = qualifiedName.appendRight(declarationValue).shiftRight()

                if (ownerContext.owner?.node is VariableModel) {
                    val variableModel = ownerContext.owner?.node as VariableModel

                    if (qualifiedPath.matchesLeft(variableModel.name)) {
                        val pathShifted = qualifiedPath?.shiftLeft()
                        if (pathShifted is IdentifierNode) {
                            val supposedModule = variableModel.name.appendRight(qualifiedName)

                            declarationResolver.resolveStrict(pathShifted.value, supposedModule.process(::unescape))?.let {
                                return TypeValueModel(supposedModule.appendRight(pathShifted), emptyList(), null)
                            }
                        }
                    }
                }


                declarationResolver.resolveStrict(declarationValue.right.value, qualifiedPath)?.let { declarationOwnerContext ->
                    val declarationQualifiedName = declarationOwnerContext.getQualifiedName()

                    if (declarationQualifiedName != qualifiedName) {
                        val qualifiedNode = qualifiedPath?.shiftLeft()?.appendRight(declarationValue.right)
                        return TypeValueModel(qualifiedNode!!, emptyList(), null)
                    }
                }

            }
        }
        return super.lowerTypeNode(ownerContext)
    }

    override fun lowerHeritageNode(ownerContext: NodeOwner<HeritageModel>): HeritageModel {
        val heritageClause = ownerContext.node
        val heritageClauseValue = heritageClause.value
        val name = heritageClauseValue

        if ((heritageClauseValue is TypeValueModel) && (heritageClauseValue.value is IdentifierNode)) {
            val name = (heritageClauseValue.value as IdentifierNode)
            declarationResolver.resolve(name.translate(), ownerContext.getQualifiedName())?.let { declarationOwnerContext ->
                val declarationQualifiedName = declarationOwnerContext.getQualifiedName()
                if (declarationQualifiedName != ownerContext.getQualifiedName()) {
                    // TODO: use QualifierNode instead of IdentifierNode
                    val qualifiedNode = name.appendRight(declarationQualifiedName.shiftLeft())
                    return heritageClause.copy(value = TypeValueModel(IdentifierNode(qualifiedNode.debugTranslate()), emptyList(), null))
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