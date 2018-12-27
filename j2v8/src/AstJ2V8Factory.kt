package org.jetbrains.dukat.ast.j2v8

import com.eclipsesource.v8.V8
import com.eclipsesource.v8.V8Object
import com.eclipsesource.v8.utils.V8ObjectUtils
import org.jetbrains.dukat.ast.AstNodeFactory
import org.jetbrains.dukat.ast.Declaration
import org.jetbrains.dukat.ast.ParameterDeclaration
import org.jetbrains.dukat.ast.TypeDeclaration

class AstJ2V8Factory(private val runtime: V8, private val astFactory: AstNodeFactory<Map<String, Any?>> = AstMapFactory()) : AstNodeFactory<V8Object> {

    fun toV8(node: Map<String, Any?>) = V8ObjectUtils.toV8Object(runtime, node)

    override fun declareVariable(name: String, type: TypeDeclaration)
        = toV8(astFactory.declareVariable(name, type))

    override fun createFunctionDeclaration(name: String, parameters: Array<ParameterDeclaration>, type: TypeDeclaration)
        = toV8(astFactory.createFunctionDeclaration(name, parameters, type))

    override fun createTypeDeclaration(value: String) = toV8(astFactory.createTypeDeclaration(value))

    override fun createGenericTypeDeclaration(value: String, params: Array<TypeDeclaration>)
        = toV8(astFactory.createGenericTypeDeclaration(value, params))

    override fun createParameterDeclaration(name: String, type: TypeDeclaration)
        = toV8(astFactory.createParameterDeclaration(name, type))

    override fun createDocumentRoot(declarations: Array<Declaration>)
        = toV8(astFactory.createDocumentRoot(declarations))
}