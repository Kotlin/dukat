package org.jetbrains.dukat.commonLowerings.merge

import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astCommon.QualifierEntity
import org.jetbrains.dukat.astCommon.appendLeft
import org.jetbrains.dukat.astCommon.appendRight
import org.jetbrains.dukat.astCommon.process
import org.jetbrains.dukat.astCommon.shiftRight
import org.jetbrains.dukat.astCommon.size
import org.jetbrains.dukat.astModel.HeritageModel
import org.jetbrains.dukat.astModel.ModuleModel
import org.jetbrains.dukat.astModel.SourceSetModel
import org.jetbrains.dukat.astModel.TypeModel
import org.jetbrains.dukat.astModel.TypeValueModel
import org.jetbrains.dukat.astModel.VariableModel
import org.jetbrains.dukat.astModel.transform
import org.jetbrains.dukat.model.commonLowerings.ModelWithOwnerTypeLowering
import org.jetbrains.dukat.ownerContext.NodeOwner

private fun NameEntity.shiftLeft(): NameEntity {
    if (this is QualifierEntity) {
        return when (left) {
            is IdentifierEntity -> right
            is QualifierEntity -> QualifierEntity((left as QualifierEntity).right, right)
        }
    }

    return this
}

private fun NameEntity?.matchesLeft(identifier: NameEntity): Boolean {
    return when (this) {
        is IdentifierEntity -> identifier == this
        is QualifierEntity -> left.matchesLeft(identifier)
        else -> false
    }
}


private fun unescape(name: String): String {
    return name.replace("(?:^`)|(?:`$)".toRegex(), "")
}

private class SpecifyTypeNodes(private val declarationResolver: DeclarationResolver) : ModelWithOwnerTypeLowering {

    override fun lowerTypeModel(ownerContext: NodeOwner<TypeModel>): TypeModel {
        val declaration = ownerContext.node
        val qualifiedName = ownerContext.getQualifiedName()

        if (declaration is TypeValueModel) {
            val declarationValue = declaration.value

            if (declarationValue is IdentifierEntity) {
                declarationResolver.resolve(declarationValue, qualifiedName)?.let { declarationOwnerContext ->
                    val declarationQualifiedName = declarationOwnerContext.getQualifiedName()

                    if (declarationQualifiedName.size > 1) {
                        if (declarationQualifiedName != qualifiedName) {
                            val qualifiedNode = declarationValue.appendRight(declarationQualifiedName).shiftLeft()
                            return TypeValueModel(qualifiedNode, declaration.params, null, null, declaration.nullable)
                        }
                    }
                }
            } else if (declarationValue is QualifierEntity) {
                // TODO: Double check deeply nested qualifiedNames
                val qualifiedPath = qualifiedName.appendLeft(declarationValue).shiftRight()

                if (ownerContext.owner?.node is VariableModel) {
                    val variableModel = ownerContext.owner?.node as VariableModel

                    if (qualifiedPath.matchesLeft(variableModel.name)) {
                        val pathShifted = qualifiedPath?.shiftLeft()
                        if (pathShifted is IdentifierEntity) {
                            val supposedModule = variableModel.name.appendRight(qualifiedName)

                            declarationResolver.resolveStrict(pathShifted, supposedModule.process(::unescape))?.let {
                                return TypeValueModel(supposedModule.appendRight(pathShifted), declaration.params, null, null, declaration.nullable)
                            }
                        }
                    }
                }


                declarationResolver.resolveStrict(declarationValue.right, qualifiedPath)?.let { declarationOwnerContext ->
                    val declarationQualifiedName = declarationOwnerContext.getQualifiedName()

                    if (declarationQualifiedName != qualifiedName) {
                        val qualifiedNode = qualifiedPath?.shiftLeft()?.appendLeft(declarationValue.right)
                                ?: declarationValue.right

                        return TypeValueModel(qualifiedNode, declaration.params, null, null, declaration.nullable)
                    }
                }

            }
        }
        return super.lowerTypeModel(ownerContext)
    }

    override fun lowerHeritageModel(ownerContext: NodeOwner<HeritageModel>): HeritageModel {
        val heritageClause = ownerContext.node
        val heritageClauseValue = heritageClause.value

        if (heritageClauseValue.value is IdentifierEntity) {
            val name = heritageClauseValue.value
            declarationResolver.resolve(name, ownerContext.getQualifiedName())?.let { declarationOwnerContext ->
                val declarationQualifiedName = declarationOwnerContext.getQualifiedName()
                if (declarationQualifiedName != ownerContext.getQualifiedName()) {
                    val qualifiedNode = name.appendRight(declarationQualifiedName.shiftLeft())
                    return heritageClause.copy(value = TypeValueModel(qualifiedNode, emptyList(), null, null))
                }
            }
        }

        return super.lowerHeritageModel(ownerContext)
    }
}

fun ModuleModel.specifyTypeNodesWithModuleData(): ModuleModel {

    val declarationContext = DeclarationResolver()
    declarationContext.process(this, NodeOwner(this, null))

    return SpecifyTypeNodes(declarationContext).lowerRoot(this, NodeOwner(this, null))
}

fun SourceSetModel.specifyTypeNodesWithModuleData() = transform { it.specifyTypeNodesWithModuleData() }