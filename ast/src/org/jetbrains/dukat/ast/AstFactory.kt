package org.jetbrains.dukat.ast

import org.jetbrains.dukat.ast.model.AstNode
import org.jetbrains.dukat.ast.model.Declaration
import org.jetbrains.dukat.ast.model.DocumentRoot
import org.jetbrains.dukat.ast.model.Expression
import org.jetbrains.dukat.ast.model.FunctionDeclaration
import org.jetbrains.dukat.ast.model.FunctionTypeDeclaration
import org.jetbrains.dukat.ast.model.ParameterDeclaration
import org.jetbrains.dukat.ast.model.ParameterValue
import org.jetbrains.dukat.ast.model.TypeDeclaration
import org.jetbrains.dukat.ast.model.TypeParameter
import org.jetbrains.dukat.ast.model.VariableDeclaration

class AstFactory: AstNodeFactory<AstNode> {

    override fun declareVariable(name: String, type: ParameterValue) = VariableDeclaration(name, type)

    override fun createExpression(kind: TypeDeclaration, meta: String?) = Expression(kind, meta)

    override fun createFunctionDeclaration(
            name: String,
            parameters: Array<ParameterDeclaration>,
            type: ParameterValue,
            typeParameters: Array<TypeParameter>
    )
            = FunctionDeclaration(name, parameters, type, typeParameters)

    override fun createFunctionTypeDeclaration(parameters: Array<ParameterDeclaration>, type: ParameterValue)
        = FunctionTypeDeclaration(parameters, type)

    override fun createTypeDeclaration(value: String, params: Array<ParameterValue>) = TypeDeclaration(value, params)

    override fun createParameterDeclaration(name: String, type: ParameterValue, initializer: Expression?)
            = ParameterDeclaration(name, type, initializer)

    override fun createDocumentRoot(declarations: Array<Declaration>) = DocumentRoot(declarations)

    override fun createTypeParam(name: String, constraints: Array<ParameterValue>) = TypeParameter(name, constraints)
}