package org.jetrbains.dukat.nodeLowering

import org.jetbrains.dukat.ast.model.duplicate
import org.jetbrains.dukat.ast.model.nodes.ClassNode
import org.jetbrains.dukat.ast.model.nodes.DocumentRootNode
import org.jetbrains.dukat.ast.model.nodes.FunctionNode
import org.jetbrains.dukat.ast.model.nodes.FunctionTypeNode
import org.jetbrains.dukat.ast.model.nodes.InterfaceNode
import org.jetbrains.dukat.ast.model.nodes.MemberNode
import org.jetbrains.dukat.ast.model.nodes.ObjectNode
import org.jetbrains.dukat.ast.model.nodes.ParameterNode
import org.jetbrains.dukat.ast.model.nodes.TypeAliasNode
import org.jetbrains.dukat.ast.model.nodes.UnionTypeNode
import org.jetbrains.dukat.ast.model.nodes.TypeValueNode
import org.jetbrains.dukat.ast.model.nodes.VariableNode
import org.jetbrains.dukat.astCommon.AstTopLevelEntity
import org.jetbrains.dukat.astCommon.AstTypeEntity
import org.jetbrains.dukat.tsmodel.ClassLikeDeclaration
import org.jetbrains.dukat.tsmodel.TypeParameterDeclaration
import org.jetbrains.dukat.tsmodel.types.IntersectionTypeDeclaration
import org.jetbrains.dukat.tsmodel.types.TupleDeclaration
import org.jetbrains.dukat.tsmodel.types.UnionTypeDeclaration

interface Lowering<T:AstTypeEntity> {
    fun lowerVariableNode(declaration: VariableNode): VariableNode
    fun lowerFunctionNode(declaration: FunctionNode): FunctionNode
    fun lowerClassNode(declaration: ClassNode): ClassNode
    fun lowerInterfaceNode(declaration: InterfaceNode): InterfaceNode

    fun lowerParameterNode(declaration: ParameterNode): ParameterNode
    fun lowerTypeParameter(declaration: TypeParameterDeclaration): TypeParameterDeclaration
    fun lowerMemberNode(declaration: MemberNode): MemberNode
    fun lowerTypeAliasNode(declaration: TypeAliasNode): TypeAliasNode
    fun lowerObjectNode(declaration: ObjectNode): ObjectNode

    fun lowerTypeNode(declaration: TypeValueNode): T
    fun lowerFunctionNode(declaration: FunctionTypeNode): T
    fun lowerUnionTypeDeclaration(declaration: UnionTypeDeclaration): T
    fun lowerUnionTypeNode(declaration: UnionTypeNode): T
    fun lowerIntersectionTypeDeclaration(declaration: IntersectionTypeDeclaration): T
    fun lowerTupleDeclaration(declaration: TupleDeclaration): T

    fun lowerClassLikeDeclaration(declaration: ClassLikeDeclaration): ClassLikeDeclaration {
        return when (declaration) {
            is InterfaceNode -> lowerInterfaceNode(declaration)
            is ClassNode -> lowerClassNode(declaration)
            else -> declaration
        }
    }

    fun lowerTopLevelEntity(declaration: AstTopLevelEntity): AstTopLevelEntity {
        return when (declaration) {
            is VariableNode -> lowerVariableNode(declaration)
            is FunctionNode -> lowerFunctionNode(declaration)
            is ClassLikeDeclaration -> lowerClassLikeDeclaration(declaration)
            is DocumentRootNode -> lowerDocumentRoot(declaration)
            is TypeAliasNode -> lowerTypeAliasNode(declaration)
            is ObjectNode -> lowerObjectNode(declaration)
            else -> declaration.duplicate()
        }
    }

    fun lowerTopLevelDeclarations(declarations: List<AstTopLevelEntity>): List<AstTopLevelEntity> {
        return declarations.map { declaration ->
            lowerTopLevelEntity(declaration)
        }
    }

    fun lowerDocumentRoot(documentRoot: DocumentRootNode): DocumentRootNode {
        return documentRoot.copy(
                declarations = lowerTopLevelDeclarations(documentRoot.declarations)
        )
    }
}
