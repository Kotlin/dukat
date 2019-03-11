package org.jetbrains.dukat.compiler.lowerings

import org.jetbrains.dukat.ast.model.nodes.DocumentRootNode
import org.jetbrains.dukat.ast.model.nodes.HeritageNode
import org.jetbrains.dukat.ast.model.nodes.HeritageSymbolNode
import org.jetbrains.dukat.ast.model.nodes.IdentifierNode
import org.jetbrains.dukat.ast.model.nodes.PropertyAccessNode
import org.jetbrains.dukat.ast.model.nodes.QualifiedLeftNode
import org.jetbrains.dukat.ast.model.nodes.QualifiedNode
import org.jetbrains.dukat.ast.model.nodes.SourceSetNode
import org.jetbrains.dukat.ast.model.nodes.transform
import org.jetbrains.dukat.tsmodel.IdentifierDeclaration
import org.jetbrains.dukat.tsmodel.QualifiedLeftDeclaration
import org.jetbrains.dukat.tsmodel.QualifiedNamedDeclaration
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration

private class LowerQualifiedDeclarations(private val moduleNode: DocumentRootNode) : ModuleAwareTypeLowering(moduleNode) {

    private fun resolve(value: QualifiedLeftDeclaration): QualifiedLeftNode {
        return when (value) {
            is IdentifierDeclaration -> {
                val reference = moduleNode.imports.get(value.value)
                when (reference) {
                    is IdentifierNode -> reference
                    else -> IdentifierNode(value.value)
                }
            }
            is QualifiedNamedDeclaration -> {
                QualifiedNode(resolve(value.left), IdentifierNode(value.right.value))
            }
            else -> throw Exception("unknown QualifiedLeftDeclaration subtype")
        }
    }

    private fun resolveExression(heritageSymbol: HeritageSymbolNode): HeritageSymbolNode {
        return when (heritageSymbol) {
            is IdentifierNode -> {
                val reference = moduleNode.imports.get(heritageSymbol.value)
                when (reference) {
                    is IdentifierNode -> reference
                    else -> heritageSymbol
                }
            }
            is PropertyAccessNode -> {
                return heritageSymbol.copy(expression = resolveExression(heritageSymbol.expression))
            }
            else -> heritageSymbol
        }
    }

    override fun lowerParameterValue(declaration: ParameterValueDeclaration): ParameterValueDeclaration {
        return when (declaration) {
            is QualifiedNamedDeclaration -> QualifiedNode(resolve(declaration.left), IdentifierNode(declaration.right.value))
            else -> declaration
        }
    }

    override fun lowerHeritageNode(heritageClause: HeritageNode): HeritageNode {
        return heritageClause.copy(name = resolveExression(heritageClause.name))
    }

    override fun lowerDocumentRoot(documentRoot: DocumentRootNode): DocumentRootNode {
        return LowerQualifiedDeclarations(documentRoot).lower()
    }
}

fun DocumentRootNode.introduceQualifiedNode(): DocumentRootNode {
    return LowerQualifiedDeclarations(this).lower()
}

fun SourceSetNode.introduceQualifiedNode() = transform { it.introduceQualifiedNode() }