package org.jetbrains.dukat.compiler.lowerings

import org.jetbrains.dukat.ast.model.makeNullable
import org.jetbrains.dukat.ast.model.nodes.DocumentRootNode
import org.jetbrains.dukat.ast.model.nodes.FunctionTypeNode
import org.jetbrains.dukat.ast.model.nodes.ParameterNode
import org.jetbrains.dukat.ast.model.nodes.SourceSetNode
import org.jetbrains.dukat.ast.model.nodes.TypeValueNode
import org.jetbrains.dukat.ast.model.nodes.UnionTypeNode
import org.jetbrains.dukat.ast.model.nodes.metadata.MuteMetadata
import org.jetbrains.dukat.ast.model.nodes.transform
import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.panic.raiseConcern
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration
import org.jetrbains.dukat.nodeLowering.NodeTypeLowering


private class LowerNullable : NodeTypeLowering {

    override fun lowerParameterNode(declaration: ParameterNode): ParameterNode {
        val type = if (declaration.optional) declaration.type.makeNullable() else declaration.type
        return declaration.copy(type = lowerType(type))
    }

    override fun lowerType(declaration: ParameterValueDeclaration): ParameterValueDeclaration {
        return when (declaration) {
            is UnionTypeNode -> {
                val params = declaration.params.filter { param ->
                    param != TypeValueNode(IdentifierEntity("undefined"), emptyList()) &&
                            param != TypeValueNode(IdentifierEntity("null"), emptyList())
                }

                if (params.size == 1) {
                    val nullableType = params[0]
                    return when (nullableType) {
                        is TypeValueNode -> {
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
                        else -> raiseConcern("can not lower nullables for unknown param type ${nullableType}") {
                            lowerUnionTypeNode(declaration)
                        }
                    }
                } else lowerUnionTypeNode(declaration)
            }
            else -> super.lowerType(declaration)
        }
    }
}

fun DocumentRootNode.lowerNullable(): DocumentRootNode {
    return LowerNullable().lowerDocumentRoot(this)
}

fun SourceSetNode.lowerNullable() = transform { it.lowerNullable() }