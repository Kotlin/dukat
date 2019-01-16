package org.jetbrains.dukat.ast.j2v8

import org.jetbrains.dukat.ast.astToMap
import org.jetbrains.dukat.ast.factory.AstFactory
import org.jetbrains.dukat.ast.factory.AstNodeFactory
import org.jetbrains.dukat.ast.model.AstNode
import org.jetbrains.dukat.ast.model.ClassLikeDeclaration
import org.jetbrains.dukat.ast.model.Declaration
import org.jetbrains.dukat.ast.model.Expression
import org.jetbrains.dukat.ast.model.InterfaceDeclaration
import org.jetbrains.dukat.ast.model.MemberDeclaration
import org.jetbrains.dukat.ast.model.ParameterDeclaration
import org.jetbrains.dukat.ast.model.ParameterValue
import org.jetbrains.dukat.ast.model.TypeDeclaration
import org.jetbrains.dukat.ast.model.TypeParameter

class AstMapFactory(private val astFactory: AstNodeFactory<AstNode> = AstFactory()) : AstNodeFactory<Map<String, Any?>> {
    override fun createClassDeclaration(
            name: String,
            members: List<MemberDeclaration>,
            typeParameters: List<TypeParameter>,
            parentEntities: List<ClassLikeDeclaration>
    ) = astFactory.createClassDeclaration(name, members, typeParameters, parentEntities).astToMap()

    override fun createInterfaceDeclaration(
            name: String,
            members: List<MemberDeclaration>,
            typeParameters: List<TypeParameter>,
            parentEntities: List<InterfaceDeclaration>
    ) = astFactory.createInterfaceDeclaration(name, members, typeParameters, parentEntities).astToMap()

    override fun createExpression(kind: TypeDeclaration, meta: String?) = astFactory.createExpression(kind, meta).astToMap()

    override fun declareVariable(name: String, type: ParameterValue) = astFactory.declareVariable(name, type).astToMap()

    override fun declareProperty(name: String, type: ParameterValue, parameters: List<TypeParameter>, getter: Boolean, setter: Boolean)
            = astFactory.declareProperty(name, type, parameters, getter, setter).astToMap()

    override fun createFunctionDeclaration(name: String, parameters: Array<ParameterDeclaration>, type: ParameterValue, typeParameters: Array<TypeParameter>): Map<String, Any?>
            = astFactory.createFunctionDeclaration(name, parameters, type, typeParameters).astToMap()

    override fun createMethodDeclaration(
            name: String,
            parameters: List<ParameterDeclaration>,
            type: ParameterValue, typeParameters: List<TypeParameter>,
            override: Boolean,
            operator: Boolean
    )= astFactory.createMethodDeclaration(name, parameters, type, typeParameters, override, operator).astToMap()

    override fun createFunctionTypeDeclaration(parameters: Array<ParameterDeclaration>, type: ParameterValue) = astFactory.createFunctionTypeDeclaration(parameters, type).astToMap()

    override fun createTypeDeclaration(value: String, params: Array<ParameterValue>) = astFactory.createTypeDeclaration(value, params).astToMap()

    override fun createParameterDeclaration(name: String, type: ParameterValue, initializer: Expression?) = astFactory.createParameterDeclaration(name, type, initializer).astToMap()

    override fun createDocumentRoot(packageName: String, declarations: Array<Declaration>) = astFactory.createDocumentRoot(packageName, declarations).astToMap()

    override fun createTypeParam(name: String, constraints: Array<ParameterValue>) = astFactory.createTypeParam(name, constraints).astToMap()
}
