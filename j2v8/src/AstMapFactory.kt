package org.jetbrains.dukat.ast.j2v8

import org.jetbrains.dukat.ast.AstFactory
import org.jetbrains.dukat.ast.AstNodeFactory
import org.jetbrains.dukat.ast.astToMap
import org.jetbrains.dukat.ast.model.AstNode
import org.jetbrains.dukat.ast.model.Declaration
import org.jetbrains.dukat.ast.model.Expression
import org.jetbrains.dukat.ast.model.ParameterDeclaration
import org.jetbrains.dukat.ast.model.ParameterValue
import org.jetbrains.dukat.ast.model.TypeDeclaration
import org.jetbrains.dukat.ast.model.TypeParameter

class AstMapFactory(private val astFactory: AstNodeFactory<AstNode> = AstFactory()) : AstNodeFactory<Map<String, Any?>> {
    override fun createExpression(kind: TypeDeclaration, meta: String?) = astFactory.createExpression(kind, meta).astToMap()

    override fun declareVariable(name: String, type: TypeDeclaration) = astFactory.declareVariable(name, type).astToMap()

    override fun createFunctionDeclaration(name: String, parameters: Array<ParameterDeclaration>, type: ParameterValue, typeParameters: Array<TypeParameter>): Map<String, Any?> = astFactory.createFunctionDeclaration(name, parameters, type, typeParameters).astToMap()

    override fun createFunctionTypeDeclaration(parameters: Array<ParameterDeclaration>, type: ParameterValue) = astFactory.createFunctionTypeDeclaration(parameters, type).astToMap()

    override fun createTypeDeclaration(value: String, params: Array<ParameterValue>) = astFactory.createTypeDeclaration(value, params).astToMap()

    override fun createParameterDeclaration(name: String, type: ParameterValue, initializer: Expression?) = astFactory.createParameterDeclaration(name, type, initializer).astToMap()

    override fun createDocumentRoot(declarations: Array<Declaration>) = astFactory.createDocumentRoot(declarations).astToMap()

    override fun createTypeParam(name: String) = astFactory.createTypeParam(name).astToMap()
}
