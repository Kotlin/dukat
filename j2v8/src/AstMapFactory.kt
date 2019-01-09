package org.jetbrains.dukat.ast.j2v8

import org.jetbrains.dukat.ast.AstFactory
import org.jetbrains.dukat.ast.AstNodeFactory
import org.jetbrains.dukat.ast.astToMap
import org.jetbrains.dukat.ast.model.AstNode
import org.jetbrains.dukat.ast.model.Declaration
import org.jetbrains.dukat.ast.model.Expression
import org.jetbrains.dukat.ast.model.ParameterDeclaration
import org.jetbrains.dukat.ast.model.TypeDeclaration

class AstMapFactory(private val astFactory: AstNodeFactory<AstNode> = AstFactory()) : AstNodeFactory<Map<String, Any?>> {
    override fun createExpression(kind: TypeDeclaration, meta: String?) = astFactory.createExpression(kind, meta).astToMap()

    override fun declareVariable(name: String, type: TypeDeclaration) = astFactory.declareVariable(name, type).astToMap()

    override fun createFunctionDeclaration(name: String, parameters: Array<ParameterDeclaration>, type: TypeDeclaration): Map<String, Any?>
        = astFactory.createFunctionDeclaration(name, parameters, type).astToMap()

    override fun createTypeDeclaration(value: String, params: Array<TypeDeclaration>)
        = astFactory.createTypeDeclaration(value, params).astToMap()

    override fun createParameterDeclaration(name: String, type: TypeDeclaration, initializer: Expression?)
        = astFactory.createParameterDeclaration(name, type, initializer).astToMap()

    override fun createDocumentRoot(declarations: Array<Declaration>)
        = astFactory.createDocumentRoot(declarations).astToMap()
}
