package org.jetbrains.dukat.ast

interface AstNodeFactory<T> {
    fun declareVariable(name: String, type: TypeDeclaration): T
    fun createFunctionDeclaration(name: String, parameters: Array<ParameterDeclaration>, type: TypeDeclaration): T
    fun createTypeDeclaration(value: String): T
    fun createGenericTypeDeclaration(value: String, params: Array<TypeDeclaration>): T
    fun createParameterDeclaration(name: String, type: TypeDeclaration): T
    fun createDocumentRoot(declarations: Array<Declaration>): T
}

class AstFactory: AstNodeFactory<AstNode> {
    override fun declareVariable(name: String, type: TypeDeclaration) = VariableDeclaration(name, type)

    override fun createFunctionDeclaration(name: String, parameters: Array<ParameterDeclaration>, type: TypeDeclaration) = FunctionDeclaration(name, parameters, type)

    override fun createTypeDeclaration(value: String) = TypeDeclaration(value, arrayOf())

    override fun createGenericTypeDeclaration(value: String, params: Array<TypeDeclaration>) = TypeDeclaration(value, params)

    override fun createParameterDeclaration(name: String, type: TypeDeclaration) = ParameterDeclaration(name, type)

    override fun createDocumentRoot(declarations: Array<Declaration>) = DocumentRoot(declarations)
}