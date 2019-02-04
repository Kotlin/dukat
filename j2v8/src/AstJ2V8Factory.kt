package org.jetbrains.dukat.ast.j2v8

import com.eclipsesource.v8.V8
import com.eclipsesource.v8.V8Object
import com.eclipsesource.v8.utils.V8ObjectUtils
import org.jetbrains.dukat.astCommon.MemberDeclaration
import org.jetbrains.dukat.astCommon.TopLevelDeclaration
import org.jetbrains.dukat.tsmodel.ExpressionDeclaration
import org.jetbrains.dukat.tsmodel.HeritageClauseDeclaration
import org.jetbrains.dukat.tsmodel.ModifierDeclaration
import org.jetbrains.dukat.tsmodel.ParameterDeclaration
import org.jetbrains.dukat.tsmodel.TokenDeclaration
import org.jetbrains.dukat.tsmodel.TypeParameterDeclaration
import org.jetbrains.dukat.tsmodel.factory.AstNodeFactory
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration
import org.jetbrains.dukat.tsmodel.types.TypeDeclaration


class AstJ2V8Factory(private val runtime: V8, private val astFactory: AstNodeFactory<Map<String, Any?>> = AstMapFactory()) : AstNodeFactory<V8Object> {

    private fun toV8(node: Map<String, Any?>) = V8ObjectUtils.toV8Object(runtime, node)

    override fun createExportAssignmentDeclaration(name: String) = toV8(astFactory.createExportAssignmentDeclaration(name))

    override fun createTokenDeclaration(value: String) = toV8(astFactory.createTokenDeclaration(value))

    override fun createHeritageClauseDeclaration(name: String, typeArguments: List<TokenDeclaration>, extending: Boolean)
        =   toV8(astFactory.createHeritageClauseDeclaration(name, typeArguments, extending))

    override fun createTypeAliasDeclaration(aliasName: String, typeParameters: List<TokenDeclaration>, typeReference: ParameterValueDeclaration)
        = toV8(astFactory.createTypeAliasDeclaration(aliasName, typeParameters, typeReference))

    override fun createStringTypeDeclaration(tokens: List<String>) =
            toV8(astFactory.createStringTypeDeclaration(tokens))

    override fun createIndexSignatureDeclaration(indexTypes: List<ParameterDeclaration>, returnType: ParameterValueDeclaration) =
            toV8(astFactory.createIndexSignatureDeclaration(indexTypes, returnType))

    override fun createCallSignatureDeclaration(
            parameters: List<ParameterDeclaration>,
            type: ParameterValueDeclaration,
            typeParameters: List<TypeParameterDeclaration>
    ) = toV8(astFactory.createCallSignatureDeclaration(parameters, type, typeParameters))

    override fun createModifierDeclaration(token: String) = toV8(astFactory.createModifierDeclaration(token))

    override fun createClassDeclaration(
            name: String,
            members: List<MemberDeclaration>,
            typeParameters: List<TypeParameterDeclaration>,
            parentEntities: List<HeritageClauseDeclaration>,
            modifiers: List<ModifierDeclaration>
    ): V8Object = toV8(astFactory.createClassDeclaration(name, members, typeParameters, parentEntities, modifiers))

    override fun createObjectLiteral(members: List<MemberDeclaration>) = toV8(astFactory.createObjectLiteral(members))

    override fun createInterfaceDeclaration(
            name: String,
            members: List<MemberDeclaration>,
            typeParameters: List<TypeParameterDeclaration>,
            parentEntities: List<HeritageClauseDeclaration>
    ): V8Object = toV8(astFactory.createInterfaceDeclaration(name, members, typeParameters, parentEntities))

    override fun createExpression(kind: TypeDeclaration, meta: String?) = toV8(astFactory.createExpression(kind, meta))

    override fun declareVariable(name: String, type: ParameterValueDeclaration, modifiers: List<ModifierDeclaration>): V8Object = toV8(astFactory.declareVariable(name, type, modifiers))

    override fun declareProperty(
            name: String,
            type: ParameterValueDeclaration,
            parameters: List<TypeParameterDeclaration>,
            optional: Boolean,
            modifiers: List<ModifierDeclaration>
    )
            = toV8(astFactory.declareProperty(name, type, parameters, optional, modifiers))

    override fun createConstructorDeclaration(parameters: List<ParameterDeclaration>, type: ParameterValueDeclaration, typeParameters: List<TypeParameterDeclaration>, modifiers: List<ModifierDeclaration>)
        = toV8(astFactory.createConstructorDeclaration(parameters, type, typeParameters, modifiers))

    override fun createFunctionDeclaration(
            name: String,
            parameters: Array<ParameterDeclaration>,
            type: ParameterValueDeclaration,
            typeParameters: Array<TypeParameterDeclaration>,
            modifiers: List<ModifierDeclaration>
    ) = toV8(astFactory.createFunctionDeclaration(name, parameters, type, typeParameters, modifiers))

    override fun createMethodSignatureDeclaration(name: String, parameters: Array<ParameterDeclaration>, type: ParameterValueDeclaration, typeParameters: Array<TypeParameterDeclaration>, optional: Boolean, modifiers: List<ModifierDeclaration>)
        = toV8(astFactory.createMethodSignatureDeclaration(name, parameters, type, typeParameters, optional, modifiers))

    override fun createFunctionTypeDeclaration(parameters: Array<ParameterDeclaration>, type: ParameterValueDeclaration)
        = toV8(astFactory.createFunctionTypeDeclaration(parameters, type))

    override fun createUnionDeclaration(params: List<ParameterValueDeclaration>)
            = toV8(astFactory.createUnionDeclaration(params))

    override fun createTypeDeclaration(value: String, params: Array<ParameterValueDeclaration>)
        = toV8(astFactory.createTypeDeclaration(value, params))

    override fun createParameterDeclaration(name: String, type: ParameterValueDeclaration, initializer: ExpressionDeclaration?, vararg: Boolean, optional: Boolean)
        = toV8(astFactory.createParameterDeclaration(name, type, initializer, vararg, optional))

    override fun createDocumentRoot(packageName: String, declarations: Array<TopLevelDeclaration>, modifiers: List<ModifierDeclaration>)
        = toV8(astFactory.createDocumentRoot(packageName, declarations, modifiers))

    override fun createTypeParam(name: String, constraints: Array<ParameterValueDeclaration>) = toV8(astFactory.createTypeParam(name, constraints))
}