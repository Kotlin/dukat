package org.jetbrains.dukat.compiler.lowerings

import org.jetbrains.dukat.ast.model.ClassDeclaration
import org.jetbrains.dukat.ast.model.FunctionDeclaration
import org.jetbrains.dukat.ast.model.FunctionTypeDeclaration
import org.jetbrains.dukat.ast.model.InterfaceDeclaration
import org.jetbrains.dukat.ast.model.MethodDeclaration
import org.jetbrains.dukat.ast.model.ParameterDeclaration
import org.jetbrains.dukat.ast.model.PropertyDeclaration
import org.jetbrains.dukat.ast.model.TypeDeclaration
import org.jetbrains.dukat.ast.model.TypeParameter
import org.jetbrains.dukat.ast.model.VariableDeclaration
import org.jetbrains.dukat.ast.model.extended.ObjectLiteral

abstract class ParameterValueLowering : Lowering {
    override fun lowerObjectLiteral(declaration: ObjectLiteral): ObjectLiteral {
        return declaration.copy(members = declaration.members.map { member -> lowerMemberDeclaration(member) })
    }

    override fun lowerFunctionDeclaration(declaration: FunctionDeclaration): FunctionDeclaration {
        return declaration.copy(
                parameters = declaration.parameters.map { parameter -> parameter.copy(type = lowerParameterValue(parameter.type)) },
                typeParameters = declaration.typeParameters.map { typeParameter ->
                    typeParameter.copy(constraints = typeParameter.constraints.map { constraint -> lowerParameterValue(constraint) })
                },
                type = lowerParameterValue(declaration.type)
        )
    }

    override fun lowerPropertyDeclaration(declaration: PropertyDeclaration): PropertyDeclaration {
        return declaration.copy(
                type = lowerParameterValue(declaration.type),
                typeParameters = declaration.typeParameters.map {typeParameter -> lowerTypeParameter(typeParameter) }
            )
    }

    override fun lowerTypeParameter(declaration: TypeParameter): TypeParameter {
        return declaration.copy(constraints = declaration.constraints.map {constraint -> lowerParameterValue(constraint)})
    }

    override fun lowerTypeDeclaration(declaration: TypeDeclaration): TypeDeclaration {
        return declaration.copy(params = declaration.params.map { param -> lowerParameterValue(param) })
    }

    override fun lowerFunctionTypeDeclaration(declaration: FunctionTypeDeclaration): FunctionTypeDeclaration {
        return declaration.copy(
                parameters = declaration.parameters.map { param -> lowerParameterDeclaration(param) },
                type = lowerParameterValue(declaration.type)
        )
    }

    override fun lowerParameterDeclaration(declaration: ParameterDeclaration): ParameterDeclaration {
        return declaration.copy(type = lowerParameterValue(declaration.type))
    }

    override fun lowerMethodDeclaration(declaration: MethodDeclaration): MethodDeclaration {
        return declaration.copy(
                parameters = declaration.parameters.map { parameter -> lowerParameterDeclaration(parameter) },
                typeParameters = declaration.typeParameters.map { typeParameter ->
                    typeParameter.copy(constraints = typeParameter.constraints.map { constraint -> lowerParameterValue(constraint) })
                },
                type = lowerParameterValue(declaration.type)
        )
    }

    override fun lowerVariableDeclaration(declaration: VariableDeclaration): VariableDeclaration {
        return declaration.copy(type = lowerParameterValue(declaration.type))
    }

    override fun lowerInterfaceDeclaration(declaration: InterfaceDeclaration): InterfaceDeclaration {
        return declaration.copy(
                members = declaration.members.map { member -> lowerMemberDeclaration(member) },
                parentEntities = declaration.parentEntities.map { parentEntity -> lowerInterfaceDeclaration(parentEntity) },
                typeParameters = declaration.typeParameters.map {
                    typeParameter -> lowerTypeParameter(typeParameter)
                }
        )
    }

    override fun lowerClassDeclaration(declaration: ClassDeclaration): ClassDeclaration {
        return declaration.copy(
                members = declaration.members.map { member -> lowerMemberDeclaration(member) },
                staticMembers = declaration.staticMembers.map { member -> lowerMemberDeclaration(member) },
                primaryConstructor = declaration.primaryConstructor?.let { lowerMethodDeclaration(it) },
                parentEntities = declaration.parentEntities.map { parentEntity -> lowerClassLikeDeclaration(parentEntity) },
                typeParameters = declaration.typeParameters.map {
                    typeParameter -> lowerTypeParameter(typeParameter)
                }
        )
    }
}