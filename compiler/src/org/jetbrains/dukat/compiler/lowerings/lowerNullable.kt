package org.jetbrains.dukat.compiler.lowerings

import org.jetbrains.dukat.ast.model.makeNullable
import org.jetbrains.dukat.ast.model.nodes.DocumentRootNode
import org.jetbrains.dukat.ast.model.nodes.FunctionTypeNode
import org.jetbrains.dukat.ast.model.nodes.ParameterNode
import org.jetbrains.dukat.ast.model.nodes.SourceSetNode
import org.jetbrains.dukat.ast.model.nodes.ValueTypeNode
import org.jetbrains.dukat.ast.model.nodes.metadata.MuteMetadata
import org.jetbrains.dukat.ast.model.nodes.transform
import org.jetrbains.dukat.nodeLowering.ParameterValueLowering
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration
import org.jetbrains.dukat.tsmodel.types.UnionTypeDeclaration


private class LowerNullable : ParameterValueLowering {

    override fun lowerParameterNode(declaration: ParameterNode): ParameterNode {
        val type = if (declaration.optional) declaration.type.makeNullable() else declaration.type
        return declaration.copy(type = lowerParameterValue(type))
    }

    override fun lowerParameterValue(declaration: ParameterValueDeclaration): ParameterValueDeclaration {
        return when (declaration) {
            is UnionTypeDeclaration -> {
                val params = declaration.params.filter { param ->
                    param != ValueTypeNode("undefined", emptyList()) &&
                            param != ValueTypeNode("null", emptyList())
                }

                if (params.size == 1) {
                    val nullableType = params[0]
                    return when (nullableType) {
                        is ValueTypeNode -> {
                            val res = lowerTypeNode(nullableType)
                            res.nullable = true
                            res.meta = MuteMetadata()
                            res
                        }
                        is FunctionTypeNode -> {
                            val res = lowerFunctionNode(nullableType)
                            res.nullable = true
                            res.meta = MuteMetadata()
                            res
                        }
                        else -> throw Exception("can not lower nullables for unknown param type ${nullableType}")
                    }
                } else lowerUnionTypeDeclaration(declaration)
            }
            else -> super.lowerParameterValue(declaration)
        }

    }
}

fun DocumentRootNode.lowerNullable(): DocumentRootNode {
    return LowerNullable().lowerDocumentRoot(this)
}

fun SourceSetNode.lowerNullable() = transform { it.lowerNullable() }