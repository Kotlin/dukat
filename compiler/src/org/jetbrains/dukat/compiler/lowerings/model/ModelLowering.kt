package org.jetbrains.dukat.compiler.lowerings.model

import org.jetbrains.dukat.ast.model.model.ClassLikeModel
import org.jetbrains.dukat.ast.model.model.ClassModel
import org.jetbrains.dukat.ast.model.model.InterfaceModel
import org.jetbrains.dukat.ast.model.model.ModuleModel
import org.jetbrains.dukat.ast.model.nodes.EnumNode
import org.jetbrains.dukat.ast.model.nodes.FunctionNode
import org.jetbrains.dukat.ast.model.nodes.FunctionTypeNode
import org.jetbrains.dukat.ast.model.nodes.MemberNode
import org.jetbrains.dukat.ast.model.nodes.ObjectNode
import org.jetbrains.dukat.ast.model.nodes.TypeNode
import org.jetbrains.dukat.ast.model.nodes.UnionTypeNode
import org.jetbrains.dukat.ast.model.nodes.VariableNode
import org.jetbrains.dukat.astCommon.TopLevelDeclaration
import org.jetbrains.dukat.compiler.declarationContext.ClassLikeOwnerContext
import org.jetbrains.dukat.compiler.declarationContext.ClassModelOwnerContext
import org.jetbrains.dukat.compiler.declarationContext.InterfaceModelOwnerContext
import org.jetbrains.dukat.compiler.declarationContext.ModuleModelOwnerContext
import org.jetbrains.dukat.compiler.declarationContext.TypeContext
import org.jetbrains.dukat.ownerContext.OwnerContext
import org.jetbrains.dukat.tsmodel.ParameterDeclaration
import org.jetbrains.dukat.tsmodel.TypeParameterDeclaration
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration
import org.jetbrains.dukat.tsmodel.types.TupleDeclaration

interface ModelLowering {
    fun lowerVariableNode(declaration: VariableNode): VariableNode
    fun lowerFunctionNode(declaration: FunctionNode, ownerContext: ModuleModelOwnerContext): FunctionNode
    fun lowerClassModel(declaration: ClassModel, ownerContext: ClassModelOwnerContext): ClassModel
    fun lowerInterfaceModel(declaration: InterfaceModel, ownerContext: InterfaceModelOwnerContext): InterfaceModel

    fun lowerParameterDeclaration(declaration: ParameterDeclaration, ownerContext: OwnerContext): ParameterDeclaration
    fun lowerTypeParameter(declaration: TypeParameterDeclaration): TypeParameterDeclaration
    fun lowerMemberNode(declaration: MemberNode, ownerContext: ClassLikeOwnerContext): MemberNode
    fun lowerObjectNode(declaration: ObjectNode): ObjectNode
    fun lowerEnumNode(declaration: EnumNode): EnumNode


    fun lowerTypeNode(declaration: TypeNode): ParameterValueDeclaration
    fun lowerFunctionTypeNode(declaration: FunctionTypeNode): ParameterValueDeclaration
    fun lowerUnionTypeNode(declaration: UnionTypeNode): ParameterValueDeclaration
    fun lowerTupleDeclaration(declaration: TupleDeclaration): ParameterValueDeclaration

    fun lowerParameterValue(declaration: ParameterValueDeclaration, owner: TypeContext): ParameterValueDeclaration {
        return when (declaration) {
            is TypeNode -> lowerTypeNode(declaration)
            is FunctionTypeNode -> lowerFunctionTypeNode(declaration)
            is UnionTypeNode -> lowerUnionTypeNode(declaration)
            is TupleDeclaration -> lowerTupleDeclaration(declaration)
            else -> declaration
        }
    }


    fun lowerClassLikeModel(declaration: ClassLikeModel, ownerContext: ModuleModelOwnerContext): ClassLikeModel {
        return when (declaration) {
            is InterfaceModel -> lowerInterfaceModel(declaration, InterfaceModelOwnerContext(declaration, ownerContext))
            is ClassModel -> lowerClassModel(declaration, ClassModelOwnerContext(declaration, ownerContext))
            else -> declaration
        }
    }

    fun lowerTopLevelDeclaration(declaration: TopLevelDeclaration, ownerContext: ModuleModelOwnerContext): TopLevelDeclaration {
        return when (declaration) {
            is VariableNode -> lowerVariableNode(declaration)
            is FunctionNode -> lowerFunctionNode(declaration, ownerContext)
            is ClassLikeModel -> lowerClassLikeModel(declaration, ownerContext)
            is ObjectNode -> lowerObjectNode(declaration)
            is EnumNode -> lowerEnumNode(declaration)
            else -> throw Exception("unknown TopeLevelDeclaration ${declaration::class.simpleName}")
        }
    }

    fun lowerTopLevelDeclarations(declarations: List<TopLevelDeclaration>, ownerContext: ModuleModelOwnerContext): List<TopLevelDeclaration> {
        return declarations.map { declaration ->
            lowerTopLevelDeclaration(declaration, ownerContext)
        }
    }

    fun lowerRoot(moduleModel: ModuleModel, ownerContext: ModuleModelOwnerContext): ModuleModel {
        return moduleModel.copy(
                declarations = lowerTopLevelDeclarations(moduleModel.declarations, ownerContext),
                sumbodules = moduleModel.sumbodules.map { submodule -> lowerRoot(submodule, ModuleModelOwnerContext(submodule, ownerContext)) }
        )
    }
}
