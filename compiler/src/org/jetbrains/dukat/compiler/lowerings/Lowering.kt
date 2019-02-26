package org.jetbrains.dukat.compiler.lowerings

import org.jetbrains.dukat.ast.model.duplicate
import org.jetbrains.dukat.ast.model.nodes.ClassNode
import org.jetbrains.dukat.ast.model.nodes.DocumentRootNode
import org.jetbrains.dukat.ast.model.nodes.FunctionNode
import org.jetbrains.dukat.ast.model.nodes.FunctionTypeNode
import org.jetbrains.dukat.ast.model.nodes.InterfaceNode
import org.jetbrains.dukat.ast.model.nodes.MemberNode
import org.jetbrains.dukat.ast.model.nodes.ObjectNode
import org.jetbrains.dukat.ast.model.nodes.TypeNode
import org.jetbrains.dukat.ast.model.nodes.UnionTypeNode
import org.jetbrains.dukat.ast.model.nodes.VariableNode
import org.jetbrains.dukat.astCommon.TopLevelDeclaration
import org.jetbrains.dukat.tsmodel.ClassLikeDeclaration
import org.jetbrains.dukat.tsmodel.ParameterDeclaration
import org.jetbrains.dukat.tsmodel.TypeAliasDeclaration
import org.jetbrains.dukat.tsmodel.TypeParameterDeclaration
import org.jetbrains.dukat.tsmodel.types.IntersectionTypeDeclaration
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration
import org.jetbrains.dukat.tsmodel.types.TupleDeclaration
import org.jetbrains.dukat.tsmodel.types.UnionTypeDeclaration

interface Lowering {
    fun lowerVariableNode(declaration: VariableNode): VariableNode
    fun lowerFunctionNode(declaration: FunctionNode): FunctionNode
    fun lowerClassNode(declaration: ClassNode): ClassNode
    fun lowerInterfaceNode(declaration: InterfaceNode): InterfaceNode

    fun lowerParameterDeclaration(declaration: ParameterDeclaration): ParameterDeclaration
    fun lowerTypeParameter(declaration: TypeParameterDeclaration): TypeParameterDeclaration
    fun lowerMemberNode(declaration: MemberNode): MemberNode
    fun lowerTypeAliasDeclaration(declaration: TypeAliasDeclaration): TypeAliasDeclaration
    fun lowerObjectNode(declaration: ObjectNode): ObjectNode

    fun lowerTypeNode(declaration: TypeNode): ParameterValueDeclaration
    fun lowerFunctionNode(declaration: FunctionTypeNode): ParameterValueDeclaration
    fun lowerUnionTypeDeclaration(declaration: UnionTypeDeclaration): ParameterValueDeclaration
    fun lowerUnionTypeNode(declaration: UnionTypeNode): ParameterValueDeclaration
    fun lowerIntersectionTypeDeclaration(declaration: IntersectionTypeDeclaration): ParameterValueDeclaration
    fun lowerTupleDeclaration(declaration: TupleDeclaration): ParameterValueDeclaration

    fun lowerParameterValue(declaration: ParameterValueDeclaration): ParameterValueDeclaration {
        return when (declaration) {
            is TypeNode -> lowerTypeNode(declaration)
            is FunctionTypeNode -> lowerFunctionNode(declaration)
            is UnionTypeDeclaration -> lowerUnionTypeDeclaration(declaration)
            is IntersectionTypeDeclaration -> lowerIntersectionTypeDeclaration(declaration)
            is UnionTypeNode -> lowerUnionTypeNode(declaration)
            is TupleDeclaration -> lowerTupleDeclaration(declaration)
            else -> declaration
        }
    }


    fun lowerClassLikeDeclaration(declaration: ClassLikeDeclaration): ClassLikeDeclaration {
        return when (declaration) {
            is InterfaceNode -> lowerInterfaceNode(declaration)
            is ClassNode -> lowerClassNode(declaration)
            else -> declaration
        }
    }

    fun lowerTopLevelDeclaration(declaration: TopLevelDeclaration): TopLevelDeclaration {
        return when (declaration) {
            is VariableNode -> lowerVariableNode(declaration)
            is FunctionNode -> lowerFunctionNode(declaration)
            is ClassLikeDeclaration -> lowerClassLikeDeclaration(declaration)
            is DocumentRootNode -> lowerDocumentRoot(declaration)
            is TypeAliasDeclaration -> lowerTypeAliasDeclaration(declaration)
            is ObjectNode -> lowerObjectNode(declaration)
            else -> declaration.duplicate()
        }
    }

    fun lowerTopLevelDeclarations(declarations: List<TopLevelDeclaration>): List<TopLevelDeclaration> {
        return declarations.map { declaration ->
            lowerTopLevelDeclaration(declaration)
        }
    }

    fun lowerDocumentRoot(documentRoot: DocumentRootNode): DocumentRootNode {
        return documentRoot.copy(
                declarations = lowerTopLevelDeclarations(documentRoot.declarations)
        )
    }
}
