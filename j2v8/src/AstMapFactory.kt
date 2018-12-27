package org.jetbrains.dukat.ast.j2v8

import org.jetbrains.dukat.ast.AstFactory
import org.jetbrains.dukat.ast.AstNode
import org.jetbrains.dukat.ast.AstNodeFactory
import org.jetbrains.dukat.ast.Declaration
import org.jetbrains.dukat.ast.ParameterDeclaration
import org.jetbrains.dukat.ast.TypeDeclaration
import org.jetbrains.dukat.ast.astToMap

class AstMapFactory(private val astFactory: AstNodeFactory<AstNode> = AstFactory()) : AstNodeFactory<Map<String, Any?>> {
    override fun declareVariable(name: String, type: TypeDeclaration) = astFactory.declareVariable(name, type).astToMap()

    override fun createFunctionDeclaration(name: String, parameters: Array<ParameterDeclaration>, type: TypeDeclaration): Map<String, Any?>
        = astFactory.createFunctionDeclaration(name, parameters, type).astToMap()

    override fun createTypeDeclaration(value: String): Map<String, Any?>
        = astFactory.createTypeDeclaration(value).astToMap()

    override fun createGenericTypeDeclaration(value: String, params: Array<TypeDeclaration>)
        = astFactory.createGenericTypeDeclaration(value, params).astToMap()

    override fun createParameterDeclaration(name: String, type: TypeDeclaration)
        = astFactory.createParameterDeclaration(name, type).astToMap()

    override fun createDocumentRoot(declarations: Array<Declaration>)
        = astFactory.createDocumentRoot(declarations).astToMap()
}
