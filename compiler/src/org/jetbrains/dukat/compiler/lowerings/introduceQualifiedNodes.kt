package org.jetbrains.dukat.compiler.lowerings

import org.jetbrains.dukat.ast.model.nodes.DocumentRootNode
import org.jetbrains.dukat.ast.model.nodes.QualifiedNode
import org.jetbrains.dukat.tsmodel.HeritageClauseDeclaration
import org.jetbrains.dukat.tsmodel.HeritageSymbolDeclaration
import org.jetbrains.dukat.tsmodel.IdentifierDeclaration
import org.jetbrains.dukat.tsmodel.PropertyAccessDeclaration
import org.jetbrains.dukat.tsmodel.QualifiedNamedDeclaration
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration

private class LowerQualifiedDeclarations(private val moduleNode: DocumentRootNode) : ModuleAwareTypeLowering(moduleNode) {

    private fun resolve(value: ParameterValueDeclaration): ParameterValueDeclaration {
        return when (value) {
            is IdentifierDeclaration -> {
                val reference = moduleNode.imports.get(value.value)
                when (reference) {
                    is IdentifierDeclaration -> reference
                    else -> value
                }
            }
            is QualifiedNamedDeclaration -> {
                QualifiedNode(resolve(value.left), value.right)
            }
            else -> value
        }
    }

    private fun resolveExression(heritageSymbol: HeritageSymbolDeclaration): HeritageSymbolDeclaration {
        return when(heritageSymbol) {
            is IdentifierDeclaration -> {
                val reference = moduleNode.imports.get(heritageSymbol.value)
                when (reference) {
                    is IdentifierDeclaration -> reference
                    else -> heritageSymbol
                }
            }
            is PropertyAccessDeclaration -> {
                val expression = heritageSymbol.expression
                val resolvedExpression =  when(expression) {
                    is IdentifierDeclaration -> resolveExression(expression)
                    else -> heritageSymbol
                }

                return heritageSymbol.copy(expression =  resolvedExpression)
            }
            else -> heritageSymbol
        }
    }

    override fun lowerParameterValue(declaration: ParameterValueDeclaration): ParameterValueDeclaration {
        return when (declaration) {
            is QualifiedNamedDeclaration -> QualifiedNode(resolve(declaration.left), declaration.right)
            else -> declaration
        }
    }

    override fun lowerHeritageClause(heritageClause: HeritageClauseDeclaration): HeritageClauseDeclaration {
        return heritageClause.copy(name = resolveExression(heritageClause.name))
    }

    override fun lowerDocumentRoot(documentRoot: DocumentRootNode): DocumentRootNode {
        return LowerQualifiedDeclarations(documentRoot).lower()
    }
}

fun DocumentRootNode.introduceQualifiedNode() : DocumentRootNode {
    return LowerQualifiedDeclarations(this).lower()
}