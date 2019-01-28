package org.jetbrains.dukat.ast.j2v8

import org.jetbrains.dukat.ast.astToMap
import org.jetbrains.dukat.ast.factory.AstFactory
import org.jetbrains.dukat.ast.factory.AstNodeFactory
import org.jetbrains.dukat.ast.model.AstNode
import org.jetbrains.dukat.ast.model.declaration.ClassLikeDeclaration
import org.jetbrains.dukat.ast.model.declaration.Declaration
import org.jetbrains.dukat.ast.model.declaration.ExpressionDeclaration
import org.jetbrains.dukat.ast.model.declaration.InterfaceDeclaration
import org.jetbrains.dukat.ast.model.declaration.MemberDeclaration
import org.jetbrains.dukat.ast.model.declaration.ModifierDeclaration
import org.jetbrains.dukat.ast.model.declaration.ParameterDeclaration
import org.jetbrains.dukat.ast.model.declaration.TypeParameterDeclaration
import org.jetbrains.dukat.ast.model.declaration.types.ParameterValueDeclaration
import org.jetbrains.dukat.ast.model.declaration.types.TypeDeclaration

class AstMapFactory(private val astFactory: AstNodeFactory<AstNode> = AstFactory()) : AstNodeFactory<Map<String, Any?>> {

    override fun createStringTypeDeclaration(tokens: List<String>)
        = astFactory.createStringTypeDeclaration(tokens).astToMap()

    override fun createIndexSignatureDeclaration(indexTypes: List<ParameterDeclaration>, returnType: ParameterValueDeclaration)
        = astFactory.createIndexSignatureDeclaration(indexTypes, returnType).astToMap()

    override fun createCallSignatureDeclaration(
            parameters: List<ParameterDeclaration>,
            type: ParameterValueDeclaration,
            typeParameters: List<TypeParameterDeclaration>
    ) = astFactory.createCallSignatureDeclaration(parameters, type, typeParameters).astToMap()

    override fun createModifierDeclaration(token: String) = astFactory.createModifierDeclaration(token).astToMap()

    override fun createClassDeclaration(
            name: String,
            members: List<MemberDeclaration>,
            typeParameters: List<TypeParameterDeclaration>,
            parentEntities: List<ClassLikeDeclaration>,
            staticMembers: List<MemberDeclaration>
    ) = astFactory.createClassDeclaration(name, members, typeParameters, parentEntities, staticMembers).astToMap()

    override fun createObjectLiteral(members: List<MemberDeclaration>) =
            astFactory.createObjectLiteral(members).astToMap()

    override fun createInterfaceDeclaration(
            name: String,
            members: List<MemberDeclaration>,
            typeParameters: List<TypeParameterDeclaration>,
            parentEntities: List<InterfaceDeclaration>
    ) = astFactory.createInterfaceDeclaration(name, members, typeParameters, parentEntities).astToMap()

    override fun createExpression(kind: TypeDeclaration, meta: String?) = astFactory.createExpression(kind, meta).astToMap()

    override fun declareVariable(name: String, type: ParameterValueDeclaration) = astFactory.declareVariable(name, type).astToMap()

    override fun declareProperty(name: String, type: ParameterValueDeclaration, parameters: List<TypeParameterDeclaration>, optional: Boolean, modifiers: List<ModifierDeclaration>)
            = astFactory.declareProperty(name, type, parameters, optional, modifiers).astToMap()

    override fun createConstructorDeclaration(parameters: List<ParameterDeclaration>, type: ParameterValueDeclaration, typeParameters: List<TypeParameterDeclaration>, modifiers: List<ModifierDeclaration>)
        = astFactory.createConstructorDeclaration(parameters, type, typeParameters, modifiers).astToMap()

    override fun createFunctionDeclaration(
            name: String,
            parameters: Array<ParameterDeclaration>,
            type: ParameterValueDeclaration,
            typeParameters: Array<TypeParameterDeclaration>,
            modifiers: List<ModifierDeclaration>
    ): Map<String, Any?>
            = astFactory.createFunctionDeclaration(name, parameters, type, typeParameters, modifiers).astToMap()

    override fun createMethodSignatureDeclaration(
            name: String,
            parameters: Array<ParameterDeclaration>,
            type: ParameterValueDeclaration,
            typeParameters: Array<TypeParameterDeclaration>,
            optional: Boolean,
            modifiers: List<ModifierDeclaration>)
        = astFactory.createMethodSignatureDeclaration(name, parameters, type, typeParameters, optional, modifiers).astToMap()

    override fun createFunctionTypeDeclaration(parameters: Array<ParameterDeclaration>, type: ParameterValueDeclaration) = astFactory.createFunctionTypeDeclaration(parameters, type).astToMap()

    override fun createTypeDeclaration(value: String, params: Array<ParameterValueDeclaration>) = astFactory.createTypeDeclaration(value, params).astToMap()

    override fun createParameterDeclaration(name: String, type: ParameterValueDeclaration, initializer: ExpressionDeclaration?, vararg: Boolean) = astFactory.createParameterDeclaration(name, type, initializer, vararg).astToMap()

    override fun createDocumentRoot(packageName: String, declarations: Array<Declaration>) = astFactory.createDocumentRoot(packageName, declarations).astToMap()

    override fun createTypeParam(name: String, constraints: Array<ParameterValueDeclaration>) = astFactory.createTypeParam(name, constraints).astToMap()
}
