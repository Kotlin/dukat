package org.jetbrains.dukat.compiler.lowerings.model

import org.jetbrains.dukat.ast.model.model.ClassModel
import org.jetbrains.dukat.ast.model.model.InterfaceModel
import org.jetbrains.dukat.ast.model.nodes.ConstructorNode
import org.jetbrains.dukat.ast.model.nodes.EnumNode
import org.jetbrains.dukat.ast.model.nodes.FunctionNode
import org.jetbrains.dukat.ast.model.nodes.FunctionTypeNode
import org.jetbrains.dukat.ast.model.nodes.HeritageNode
import org.jetbrains.dukat.ast.model.nodes.IdentifierNode
import org.jetbrains.dukat.ast.model.nodes.MemberNode
import org.jetbrains.dukat.ast.model.nodes.MethodNode
import org.jetbrains.dukat.ast.model.nodes.ObjectNode
import org.jetbrains.dukat.ast.model.nodes.PropertyNode
import org.jetbrains.dukat.ast.model.nodes.TypeNode
import org.jetbrains.dukat.ast.model.nodes.UnionTypeNode
import org.jetbrains.dukat.ast.model.nodes.VariableNode
import org.jetbrains.dukat.compiler.declarationContext.ClassLikeOwnerContext
import org.jetbrains.dukat.compiler.declarationContext.ClassModelOwnerContext
import org.jetbrains.dukat.compiler.declarationContext.FunctionOwnerContext
import org.jetbrains.dukat.compiler.declarationContext.InterfaceModelOwnerContext
import org.jetbrains.dukat.compiler.declarationContext.IrrelevantOwnerContext
import org.jetbrains.dukat.compiler.declarationContext.MethodOwnerContext
import org.jetbrains.dukat.compiler.declarationContext.ModuleModelOwnerContext
import org.jetbrains.dukat.compiler.declarationContext.ObjectNodeOwnerContext
import org.jetbrains.dukat.compiler.declarationContext.PropertyOwnerContext
import org.jetbrains.dukat.compiler.declarationContext.TypeContext
import org.jetbrains.dukat.ownerContext.OwnerContext
import org.jetbrains.dukat.tsmodel.ParameterDeclaration
import org.jetbrains.dukat.tsmodel.TypeParameterDeclaration
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration
import org.jetbrains.dukat.tsmodel.types.TupleDeclaration

interface ModelTypeLowering : ModelLowering {

    override fun lowerEnumNode(declaration: EnumNode): EnumNode {
        return declaration
    }

    fun lowerMethodNode(declaration: MethodNode, ownerContext: ClassLikeOwnerContext): MethodNode {
        return declaration.copy(
                parameters = declaration.parameters.map { parameter -> lowerParameterDeclaration(parameter, TypeContext(MethodOwnerContext(declaration, ownerContext))) },
                typeParameters = declaration.typeParameters.map { typeParameter ->
                    typeParameter.copy(constraints = typeParameter.constraints.map { constraint -> lowerParameterValue(constraint, TypeContext(MethodOwnerContext(declaration, ownerContext))) })
                },
                type = lowerParameterValue(declaration.type, TypeContext(MethodOwnerContext(declaration, ownerContext)))
        )
    }

    fun lowerPropertyNode(declaration: PropertyNode, ownerContext: ClassLikeOwnerContext): PropertyNode {
        return declaration.copy(
                type = lowerParameterValue(declaration.type, TypeContext(PropertyOwnerContext(declaration, ownerContext))),
                typeParameters = declaration.typeParameters.map { typeParameter -> lowerTypeParameter(typeParameter) }
        )
    }

    override fun lowerMemberNode(declaration: MemberNode, ownerContext: ClassLikeOwnerContext): MemberNode {
        return when (declaration) {
            is MethodNode -> lowerMethodNode(declaration, ownerContext)
            is PropertyNode -> lowerPropertyNode(declaration, ownerContext)
            is ConstructorNode -> lowerConstructorNode(declaration)
            else -> {
                println("[WARN] skipping ${declaration}")
                declaration
            }
        }
    }

    override fun lowerFunctionNode(declaration: FunctionNode, ownerContext: ModuleModelOwnerContext): FunctionNode {
        return declaration.copy(
                parameters = declaration.parameters.map { parameter -> lowerParameterDeclaration(parameter, FunctionOwnerContext(declaration, ownerContext)) },
                typeParameters = declaration.typeParameters.map { typeParameter ->
                    typeParameter.copy(constraints = typeParameter.constraints.map { constraint -> lowerParameterValue(constraint, TypeContext(FunctionOwnerContext(declaration, ownerContext))) })
                },
                type = lowerParameterValue(declaration.type, TypeContext(FunctionOwnerContext(declaration, ownerContext)))
        )
    }

    override fun lowerTypeParameter(declaration: TypeParameterDeclaration): TypeParameterDeclaration {
        return declaration.copy(constraints = declaration.constraints.map { constraint -> val lowerParameterValue = lowerParameterValue(constraint, TypeContext(IrrelevantOwnerContext()))
            lowerParameterValue
        })
    }

    override fun lowerTupleDeclaration(declaration: TupleDeclaration): ParameterValueDeclaration {
        return declaration.copy(params = declaration.params.map { param -> lowerParameterValue(param, TypeContext(IrrelevantOwnerContext())) })
    }

    override fun lowerUnionTypeNode(declaration: UnionTypeNode): UnionTypeNode {
        return declaration.copy(params = declaration.params.map { param -> lowerParameterValue(param, TypeContext(IrrelevantOwnerContext())) })
    }

    override fun lowerTypeNode(declaration: TypeNode): TypeNode {
        return declaration.copy(params = declaration.params.map { param -> lowerParameterValue(param, TypeContext(IrrelevantOwnerContext())) })
    }

    override fun lowerFunctionTypeNode(declaration: FunctionTypeNode): FunctionTypeNode {
        return declaration.copy(
                parameters = declaration.parameters.map { param -> lowerParameterDeclaration(param, IrrelevantOwnerContext()) },
                type = lowerParameterValue(declaration.type, TypeContext(IrrelevantOwnerContext()))
        )
    }

    override fun lowerParameterDeclaration(declaration: ParameterDeclaration, ownerContext: OwnerContext): ParameterDeclaration {
        return declaration.copy(type = lowerParameterValue(declaration.type, TypeContext(ownerContext)))
    }

    override fun lowerVariableNode(declaration: VariableNode): VariableNode {
        return declaration.copy(type = lowerParameterValue(declaration.type, TypeContext(IrrelevantOwnerContext())))
    }

    fun lowerHeritageNode(heritageClause: HeritageNode, ownerContext: OwnerContext): HeritageNode {
        val typeArguments = heritageClause.typeArguments.map {
            // TODO: obviously very clumsy place
            val lowerParameterDeclaration = lowerParameterValue(TypeNode(it.value, emptyList()), TypeContext(IrrelevantOwnerContext())) as TypeNode
            lowerParameterDeclaration.value as IdentifierNode
        }
        return heritageClause.copy(typeArguments = typeArguments)
    }


    override fun lowerInterfaceModel(declaration: InterfaceModel, ownerContext: InterfaceModelOwnerContext): InterfaceModel {

        return declaration.copy(
                members
                = declaration.members.map { member -> lowerMemberNode(member, ownerContext) },
                parentEntities = declaration.parentEntities.map { heritageClause ->
                    lowerHeritageNode(heritageClause, TypeContext(ownerContext))
                },
                typeParameters = declaration.typeParameters.map { typeParameter ->
                    lowerTypeParameter(typeParameter)
                }
        )
    }


    fun lowerConstructorNode(declaration: ConstructorNode): ConstructorNode {
        return declaration.copy(
                parameters = declaration.parameters.map { parameter -> lowerParameterDeclaration(parameter, TypeContext(IrrelevantOwnerContext())) },
                typeParameters = declaration.typeParameters.map { typeParameter ->
                    typeParameter.copy(constraints = typeParameter.constraints.map { constraint -> lowerParameterValue(constraint, TypeContext(IrrelevantOwnerContext())) })
                }
        )
    }

    override fun lowerObjectNode(declaration: ObjectNode): ObjectNode {
        return declaration.copy(
                members = declaration.members.map { member -> lowerMemberNode(member, ObjectNodeOwnerContext(declaration, null)) }
        )

    }

    override fun lowerClassModel(declaration: ClassModel, ownerContext: ClassModelOwnerContext): ClassModel {
        return declaration.copy(
                members = declaration.members.map { member -> lowerMemberNode(member, ownerContext) },
                parentEntities = declaration.parentEntities.map { heritageClause ->
                    lowerHeritageNode(heritageClause, TypeContext(ownerContext))
                },
                typeParameters = declaration.typeParameters.map { typeParameter ->
                    lowerTypeParameter(typeParameter)
                }
        )
    }
}
