package org.jetbrains.dukat.ast

import org.jetbrains.dukat.ast.model.Declaration
import org.jetbrains.dukat.ast.model.Expression
import org.jetbrains.dukat.ast.model.ParameterDeclaration
import org.jetbrains.dukat.ast.model.TypeDeclaration

interface AstNodeFactory<T> {
    fun createExpression(kind: TypeDeclaration, meta: String?): T
    fun declareVariable(name: String, type: TypeDeclaration): T
    fun createFunctionDeclaration(name: String, parameters: Array<ParameterDeclaration>, type: TypeDeclaration): T
    fun createTypeDeclaration(value: String, params: Array<TypeDeclaration>): T
    fun createParameterDeclaration(name: String, type: TypeDeclaration, kind: Expression?): T
    fun createDocumentRoot(declarations: Array<Declaration>): T
}