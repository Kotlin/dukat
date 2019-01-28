package org.jetbrains.dukat.ast.factory

import org.jetbrains.dukat.ast.model.AstNode
import org.jetbrains.dukat.ast.model.declaration.CallSignatureDeclaration
import org.jetbrains.dukat.ast.model.declaration.ClassDeclaration
import org.jetbrains.dukat.ast.model.declaration.ClassLikeDeclaration
import org.jetbrains.dukat.ast.model.declaration.ConstructorDeclaration
import org.jetbrains.dukat.ast.model.declaration.Declaration
import org.jetbrains.dukat.ast.model.declaration.DocumentRootDeclaration
import org.jetbrains.dukat.ast.model.declaration.ExpressionDeclaration
import org.jetbrains.dukat.ast.model.declaration.FunctionDeclaration
import org.jetbrains.dukat.ast.model.declaration.InterfaceDeclaration
import org.jetbrains.dukat.ast.model.declaration.MemberDeclaration
import org.jetbrains.dukat.ast.model.declaration.MethodSignatureDeclaration
import org.jetbrains.dukat.ast.model.declaration.ModifierDeclaration
import org.jetbrains.dukat.ast.model.declaration.ParameterDeclaration
import org.jetbrains.dukat.ast.model.declaration.PropertyDeclaration
import org.jetbrains.dukat.ast.model.declaration.TypeParameterDeclaration
import org.jetbrains.dukat.ast.model.declaration.VariableDeclaration
import org.jetbrains.dukat.ast.model.declaration.types.FunctionTypeDeclaration
import org.jetbrains.dukat.ast.model.declaration.types.IndexSignatureDeclaration
import org.jetbrains.dukat.ast.model.declaration.types.ObjectLiteralDeclaration
import org.jetbrains.dukat.ast.model.declaration.types.ParameterValueDeclaration
import org.jetbrains.dukat.ast.model.declaration.types.StringTypeDeclaration
import org.jetbrains.dukat.ast.model.declaration.types.TypeDeclaration

class AstFactory : AstNodeFactory<AstNode> {

    override fun createStringTypeDeclaration(tokens: List<String>)
        = StringTypeDeclaration(tokens)

    override fun createIndexSignatureDeclaration(indexTypes: List<ParameterDeclaration>, returnType: ParameterValueDeclaration)
            = IndexSignatureDeclaration(indexTypes, returnType)

    override fun createCallSignatureDeclaration(
            parameters: List<ParameterDeclaration>,
            type: ParameterValueDeclaration,
            typeParameters: List<TypeParameterDeclaration>
    ) = CallSignatureDeclaration(parameters, type, typeParameters)

    override fun createModifierDeclaration(token: String) = ModifierDeclaration(token)

    override fun createClassDeclaration(
            name: String,
            members: List<MemberDeclaration>,
            typeParameters: List<TypeParameterDeclaration>,
            parentEntities: List<ClassLikeDeclaration>,
            staticMembers: List<MemberDeclaration>
    ) = ClassDeclaration(name, members, typeParameters, parentEntities, null, staticMembers)

    override fun createObjectLiteral(members: List<MemberDeclaration>) = ObjectLiteralDeclaration(members)

    override fun createInterfaceDeclaration(name: String, members: List<MemberDeclaration>, typeParameters: List<TypeParameterDeclaration>, parentEntities: List<InterfaceDeclaration>) = InterfaceDeclaration(name, members, typeParameters, parentEntities)

    override fun declareVariable(name: String, type: ParameterValueDeclaration) = VariableDeclaration(name, type)
    override fun declareProperty(
            name: String,
            type: ParameterValueDeclaration,
            parameters: List<TypeParameterDeclaration>,
            optional: Boolean,
            modifiers: List<ModifierDeclaration>
    ) = PropertyDeclaration(name, type, parameters, optional, modifiers)

    override fun createExpression(kind: TypeDeclaration, meta: String?) = ExpressionDeclaration(kind, meta)

    override fun createConstructorDeclaration(parameters: List<ParameterDeclaration>, type: ParameterValueDeclaration, typeParameters: List<TypeParameterDeclaration>, modifiers: List<ModifierDeclaration>)
        = ConstructorDeclaration(parameters, type, typeParameters, modifiers)

    override fun createFunctionDeclaration(
            name: String,
            parameters: Array<ParameterDeclaration>,
            type: ParameterValueDeclaration,
            typeParameters: Array<TypeParameterDeclaration>,
            modifiers: List<ModifierDeclaration>
    ): FunctionDeclaration {
        return FunctionDeclaration(name, parameters.toList(), type, typeParameters.toList(), modifiers)
    }

    override fun createMethodSignatureDeclaration(name: String, parameters: Array<ParameterDeclaration>, type: ParameterValueDeclaration, typeParameters: Array<TypeParameterDeclaration>, optional: Boolean, modifiers: List<ModifierDeclaration>)
        = MethodSignatureDeclaration(name, parameters.toList(), type, typeParameters.toList(), optional, modifiers)

    override fun createFunctionTypeDeclaration(parameters: Array<ParameterDeclaration>, type: ParameterValueDeclaration) = FunctionTypeDeclaration(parameters.toList(), type)

    override fun createTypeDeclaration(value: String, params: Array<ParameterValueDeclaration>) = TypeDeclaration(value, params)

    override fun createParameterDeclaration(name: String, type: ParameterValueDeclaration, initializer: ExpressionDeclaration?) = ParameterDeclaration(name, type, initializer)

    override fun createDocumentRoot(packageName: String, declarations: Array<Declaration>) = DocumentRootDeclaration(packageName, declarations.toList())

    override fun createTypeParam(name: String, constraints: Array<ParameterValueDeclaration>) = TypeParameterDeclaration(name, constraints.toList())
}