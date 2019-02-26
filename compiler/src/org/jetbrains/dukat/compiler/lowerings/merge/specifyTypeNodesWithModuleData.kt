package org.jetbrains.dukat.compiler.lowerings.merge

import org.jetbrains.dukat.ast.model.model.ModuleModel
import org.jetbrains.dukat.ast.model.nodes.HeritageNode
import org.jetbrains.dukat.ast.model.nodes.IdentifierNode
import org.jetbrains.dukat.ast.model.nodes.QualifiedLeftNode
import org.jetbrains.dukat.ast.model.nodes.QualifiedNode
import org.jetbrains.dukat.ast.model.nodes.TypeNode
import org.jetbrains.dukat.ast.model.nodes.appendRight
import org.jetbrains.dukat.ast.model.nodes.debugTranslate
import org.jetbrains.dukat.ast.model.nodes.shiftRight
import org.jetbrains.dukat.compiler.declarationContext.ModuleModelOwnerContext
import org.jetbrains.dukat.compiler.declarationContext.TypeContext
import org.jetbrains.dukat.compiler.lowerings.model.ModelTypeLowering
import org.jetbrains.dukat.ownerContext.OwnerContext
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration

private fun QualifiedLeftNode.shiftLeft(): QualifiedLeftNode {
    if (this is QualifiedNode) {
        return when (left) {
            is IdentifierNode -> right
            is QualifiedNode -> QualifiedNode((left as QualifiedNode).right, right)
            else -> throw Exception("unknown ")
        }
    }

    return this
}

private class SpecifyTypeNodes(private val declarationResolver: DeclarationResolver) : ModelTypeLowering {

    override fun lowerParameterValue(declaration: ParameterValueDeclaration, owner: TypeContext): ParameterValueDeclaration {
        if (declaration is TypeNode) {
            val declarationValue = declaration.value
            if (declarationValue is IdentifierNode) {
                declarationResolver.resolve(declarationValue.value, owner.getQualifiedName())?.let { declarationOwnerContext ->
                    val declarationQualifiedName = declarationOwnerContext.getQualifiedName()
                    if (declarationQualifiedName != IdentifierNode("__ROOT__")) {
                        if (declarationQualifiedName != owner.getQualifiedName()) {
                            val qualifiedNode = declarationValue.appendRight(declarationQualifiedName).shiftLeft()
                            return TypeNode(IdentifierNode(qualifiedNode.debugTranslate()), emptyList())
                        }
                    }
                }
            }
        } else if (declaration is QualifiedNode) {
            val qualifiedPath = owner.getQualifiedName().appendRight(declaration).shiftRight()

            declarationResolver.resolveStrict(declaration.right.value, qualifiedPath)?.let { declarationOwnerContext ->
                if (declarationOwnerContext.getQualifiedName() != owner.getQualifiedName()) {
                    val qualifiedNode = qualifiedPath?.shiftLeft()?.appendRight(declaration.right)
                    println("substituting ${declaration.debugTranslate()} with ${qualifiedNode?.debugTranslate()} ()")
                    return TypeNode(IdentifierNode(qualifiedNode!!.debugTranslate()), emptyList())
                }
            }

        }
        return super.lowerParameterValue(declaration, owner)
    }

    override fun lowerHeritageNode(heritageClause: HeritageNode, ownerContext: OwnerContext): HeritageNode {
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

        return super.lowerHeritageNode(heritageClause, ownerContext)
    }
}

fun ModuleModel.specifyTypeNodesWithModuleData(): ModuleModel {

    val declarationContext = DeclarationResolver()
    declarationContext.process(this, ModuleModelOwnerContext(this, null))

    return SpecifyTypeNodes(declarationContext).lowerRoot(this, ModuleModelOwnerContext(this, null))
}