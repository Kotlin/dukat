package org.jetbrains.dukat.ast

import org.jetbrains.dukat.ast.model.AstNode
import org.jetbrains.dukat.ast.model.Declaration
import org.jetbrains.dukat.ast.model.DocumentRoot
import org.jetbrains.dukat.ast.model.Expression
import org.jetbrains.dukat.ast.model.FunctionDeclaration
import org.jetbrains.dukat.ast.model.ParameterDeclaration
import org.jetbrains.dukat.ast.model.TypeDeclaration
import org.jetbrains.dukat.ast.model.VariableDeclaration

class AstFactory: AstNodeFactory<AstNode> {
    override fun declareVariable(name: String, type: TypeDeclaration) = VariableDeclaration(name, type)

    override fun createExpression(kind: TypeDeclaration, meta: String?) = Expression(kind, meta)

    override fun createFunctionDeclaration(name: String, parameters: Array<ParameterDeclaration>, type: TypeDeclaration) = FunctionDeclaration(name, parameters, type)

    override fun createTypeDeclaration(value: String, params: Array<TypeDeclaration>) = TypeDeclaration(value, params)

    override fun createParameterDeclaration(name: String, type: TypeDeclaration, initializer: Expression?)
            = ParameterDeclaration(name, type, initializer)

    override fun createDocumentRoot(declarations: Array<Declaration>) = DocumentRoot(declarations)
}