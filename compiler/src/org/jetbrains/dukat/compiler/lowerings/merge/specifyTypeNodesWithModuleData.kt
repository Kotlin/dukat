package org.jetbrains.dukat.compiler.lowerings.merge

import org.jetbrains.dukat.ast.model.model.ModuleModel
import org.jetbrains.dukat.ast.model.nodes.IdentifierNode
import org.jetbrains.dukat.ast.model.nodes.QualifiedNode
import org.jetbrains.dukat.ast.model.nodes.TypeNode
import org.jetbrains.dukat.compiler.declarationContext.ModuleModelOwnerContext
import org.jetbrains.dukat.compiler.declarationContext.TypeContext
import org.jetbrains.dukat.compiler.lowerings.model.ModelTypeLowering
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration

private class SpecifyTypeNodes(private val declarationContext: DeclarationResolver) : ModelTypeLowering {

    override fun lowerParameterValue(declaration: ParameterValueDeclaration, owner: TypeContext): ParameterValueDeclaration {

        if (declaration is TypeNode) {
            val declarationValue = declaration.value
            if (declarationValue is IdentifierNode) {
                val moduleModelOwnerContext = declarationContext.resolve(declarationValue.value)

                val declarationOwnerModule = moduleModelOwnerContext?.node
                val moduleOwner = owner.getModuleOwnerContext()?.node

                if (declarationOwnerModule?.shortName == moduleOwner?.shortName) {
                    val declarationOwnerQualifier = declarationOwnerModule?.qualifierName
                    val moduleQualifier = moduleOwner?.qualifierName

                    if ((declarationOwnerQualifier != null) && (declarationOwnerQualifier.isNotEmpty()) && (moduleQualifier != null)) {
                        if (declarationOwnerQualifier != moduleQualifier) {
                            // TODO: use QualifierNode instead of IdentifierNode
                            return TypeNode(IdentifierNode("${declarationOwnerQualifier}.${declarationValue.value}"), emptyList())
                        }
                    }
                }

            }
        } else if (declaration is QualifiedNode) {
            val moduleModelOwnerContext = declarationContext.resolve(declaration.right.value)
            val moduleOwner = moduleModelOwnerContext?.node
            val declarationOwnerModule = moduleModelOwnerContext?.node

            if (declarationOwnerModule?.shortName == moduleOwner?.shortName) {
                return TypeNode(IdentifierNode("${moduleOwner?.qualifierName}.${declaration.right.value}"), emptyList())
            }

        }
        return super.lowerParameterValue(declaration, owner)
    }
}

fun ModuleModel.specifyTypeNodesWithModuleData(): ModuleModel {

    val declarationContext = DeclarationResolver()
    declarationContext.process(this)

    return SpecifyTypeNodes(declarationContext).lowerRoot(this, ModuleModelOwnerContext(this))
}