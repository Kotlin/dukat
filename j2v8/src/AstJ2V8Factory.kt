package org.jetbrains.dukat.ast.j2v8

import com.eclipsesource.v8.V8
import com.eclipsesource.v8.V8Object
import com.eclipsesource.v8.utils.V8ObjectUtils
import org.jetbrains.dukat.ast.AstNodeFactory
import org.jetbrains.dukat.ast.model.Declaration
import org.jetbrains.dukat.ast.model.Expression
import org.jetbrains.dukat.ast.model.InterfaceDeclaration
import org.jetbrains.dukat.ast.model.MemberDeclaration
import org.jetbrains.dukat.ast.model.ParameterDeclaration
import org.jetbrains.dukat.ast.model.ParameterValue
import org.jetbrains.dukat.ast.model.TypeDeclaration
import org.jetbrains.dukat.ast.model.TypeParameter

class AstJ2V8Factory(private val runtime: V8, private val astFactory: AstNodeFactory<Map<String, Any?>> = AstMapFactory()) : AstNodeFactory<V8Object> {

    fun toV8(node: Map<String, Any?>) = V8ObjectUtils.toV8Object(runtime, node)

    override fun createClassDeclaration(
            name: String,
            members: List<MemberDeclaration>,
            typeParameters: List<TypeParameter>
    )
        = toV8(astFactory.createClassDeclaration(name, members, typeParameters))

    override fun createInterfaceDeclaration(
            name: String,
            members: List<MemberDeclaration>,
            typeParameters: List<TypeParameter>,
            parentEntities: List<InterfaceDeclaration>
    ) = toV8(astFactory.createInterfaceDeclaration(name, members, typeParameters, parentEntities))

    override fun createExpression(kind: TypeDeclaration, meta: String?) = toV8(astFactory.createExpression(kind, meta))

    override fun declareVariable(name: String, type: ParameterValue)
        = toV8(astFactory.declareVariable(name, type))

    override fun declareProperty(
            name: String,
            type: ParameterValue,
            parameters: List<TypeParameter>,
            getter: Boolean,
            setter: Boolean
    )
            = toV8(astFactory.declareProperty(name, type, parameters, getter, setter))

    override fun createFunctionDeclaration(name: String, parameters: Array<ParameterDeclaration>, type: ParameterValue, typeParameters: Array<TypeParameter>)
        = toV8(astFactory.createFunctionDeclaration(name, parameters, type, typeParameters))

    override fun createMethodDeclaration(name: String, parameters: List<ParameterDeclaration>, type: ParameterValue, typeParameters: List<TypeParameter>, operator: Boolean)
        = toV8(astFactory.createMethodDeclaration(name, parameters, type, typeParameters, operator))

    override fun createFunctionTypeDeclaration(parameters: Array<ParameterDeclaration>, type: ParameterValue)
        = toV8(astFactory.createFunctionTypeDeclaration(parameters, type))

    override fun createTypeDeclaration(value: String, params: Array<ParameterValue>)
        = toV8(astFactory.createTypeDeclaration(value, params))

    override fun createParameterDeclaration(name: String, type: ParameterValue, initializer: Expression?)
        = toV8(astFactory.createParameterDeclaration(name, type, initializer))

    override fun createDocumentRoot(declarations: Array<Declaration>)
        = toV8(astFactory.createDocumentRoot(declarations))

    override fun createTypeParam(name: String, constraints: Array<ParameterValue>) = toV8(astFactory.createTypeParam(name, constraints))
}