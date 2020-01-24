package org.jetrbains.dukat.nodeLowering.lowerings

import org.jetbrains.dukat.ast.model.TypeParameterNode
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
import org.jetbrains.dukat.tsmodel.types.IntersectionTypeDeclaration
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration
import org.jetrbains.dukat.nodeLowering.NodeTypeLowering


private class LowerNullable : NodeTypeLowering {

    override fun lowerParameterNode(declaration: ParameterNode): ParameterNode {
        return declaration.copy(type = lowerType(declaration.type))
    }

    override fun lowerType(declaration: ParameterValueDeclaration): ParameterValueDeclaration {
        return when (declaration) {
            is UnionTypeNode -> {
                val params = declaration.params.filter { param ->
                    when (param) {
                        is TypeValueNode -> {
                            val value = param.value
                            value != IdentifierEntity("undefined") && value != IdentifierEntity("null")
                        }
                        else -> true
                    }
                }

                if (params.size == 1) {
                    return when (val nullableType = params[0]) {
                        is TypeValueNode -> {
                            val res = lowerTypeNode(nullableType)
                            res.nullable = true
                            res.meta = MuteMetadata()
                            res
                        }
                        is TypeParameterNode -> nullableType.copy(nullable = true)
                        is FunctionTypeNode -> {
                            val res = lowerFunctionNode(nullableType)
                            res.nullable = true
                            res.meta = MuteMetadata()
                            res
                        }
                        is IntersectionTypeDeclaration -> {
                            nullableType.copy(params = nullableType.params.map {param -> lowerType(param) })
                        }
                        is UnionTypeNode -> {
                            lowerType(nullableType.copy(params = nullableType.params.map {param -> lowerType(param) }))
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