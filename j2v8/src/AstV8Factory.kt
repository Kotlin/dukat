package org.jetbrains.dukat.ast.j2v8

import com.eclipsesource.v8.V8
import com.eclipsesource.v8.V8Array
import com.eclipsesource.v8.V8Object
import com.eclipsesource.v8.utils.V8ObjectUtils
import org.jetbrains.dukat.astCommon.AstNode
import org.jetbrains.dukat.astCommon.TopLevelDeclaration
import org.jetbrains.dukat.tsmodel.ExpressionDeclaration
import org.jetbrains.dukat.tsmodel.ParameterDeclaration
import org.jetbrains.dukat.tsmodel.TypeParameterDeclaration
import org.jetbrains.dukat.tsmodel.converters.astToMap
import org.jetbrains.dukat.tsmodel.converters.toAst
import org.jetbrains.dukat.tsmodel.factory.AstFactory
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration

private fun V8Object.toMap(): Map<String, Any?> = V8ObjectUtils.toMap(this)
private fun <T : AstNode> V8Object.toAst(): T = toMap().toAst()

private fun V8Array.toArray(): Array<Map<String, Any?>> {
    val res = mutableListOf<Map<String, Any?>>()

    for (param in asIterator()) {
        val map = (param as V8Object).toMap()
        param.release()

        res.add(map)
    }

    return res.toTypedArray()
}

private fun V8Array.asIterator() = object : Iterator<Any> {
    private var myCounter = 0

    override fun hasNext() = myCounter < length()
    override fun next() = get(myCounter++)
}

private fun <T:AstNode> V8Array.toAst(): List<T> {
    return toArray().map { it.toAst<T>() }
}

class AstV8Factory(private val astFactory: AstFactory, private val runtime: V8) {

    private fun AstNode.toV8() = V8ObjectUtils.toV8Object(runtime, astToMap())

    fun createThisTypeDeclaration()
            = this.astFactory.createThisTypeDeclaration().toV8()

    fun createEnumDeclaration(name: String, values: V8Array): V8Object
        =  astFactory.createEnumDeclaration(name, values.toAst()).toV8()

    fun createEnumTokenDeclaration(value: String, meta: String): V8Object
        = astFactory.createEnumTokenDeclaration(value, meta).toV8()

    fun createExportAssignmentDeclaration(name: String, isExportEquals: Boolean) = astFactory.createExportAssignmentDeclaration(name, isExportEquals).toV8()
    fun createTokenDeclaration(value: String) = astFactory.createTokenDeclaration(value).toV8()

    fun createHeritageClauseDeclaration(name: String, typeArguments: V8Array, extending: Boolean) = astFactory.createHeritageClauseDeclaration(
            name,
            typeArguments.toAst(),
            extending
    ).toV8()

    fun createTypeAliasDeclaration(aliasName: String, typeParameters: V8Array, typeReference: V8Object) = astFactory.createTypeAliasDeclaration(
            aliasName,
            typeParameters.toAst(),
            typeReference.toAst()
    ).toV8()


    fun createStringTypeDeclaration(tokens: V8Array): V8Object {
        return astFactory.createStringTypeDeclaration(
                tokens.asIterator().asSequence().map { it as String }.toList()
        ).toV8()
    }

    fun createIndexSignatureDeclaration(indexType: V8Array, returnType: V8Object): V8Object {
        return astFactory.createIndexSignatureDeclaration(
                indexType.toAst(),
                returnType.toAst()
        ).toV8()
    }

    fun createCallSignatureDeclaration(parameters: V8Array, type: V8Object, typeParameters: V8Array): V8Object {
        return astFactory.createCallSignatureDeclaration(
                parameters.toAst(),
                type.toAst(),
                typeParameters.toAst()
        ).toV8()
    }


    fun createModifierDeclaration(token: String): V8Object
            = astFactory.createModifierDeclaration(token).toV8()

    fun createClassDeclaration(name: String, members: V8Array, typeParameters: V8Array, parentEntities: V8Array, modifiers: V8Array, uid: String): V8Object = astFactory.createClassDeclaration(
            name,
            members.toAst(),
            typeParameters.toAst(),
            parentEntities.toAst(),
            modifiers.toAst(),
            uid
    ).toV8()

    fun createObjectLiteral(members: V8Array) = astFactory.createObjectLiteral(members.toAst()).toV8()

    fun createInterfaceDeclaration(name: String, members: V8Array, typeParameters: V8Array, parentEntities: V8Array, uid: String): V8Object = astFactory.createInterfaceDeclaration(
            name,
            members.toAst(),
            typeParameters.toAst(),
            parentEntities.toAst(),
            uid
    ).toV8()

    fun createExpression(kind: V8Object, meta: String) = astFactory.createExpression(kind.toAst(), meta).toV8()

    fun declareVariable(name: String, type: V8Object, modifiers: V8Array, uid: String): V8Object =
            astFactory.declareVariable(
                    name,
                    type.toAst(),
                    modifiers.toAst(),
                    uid
            ).toV8()

    fun declareProperty(name: String, type: V8Object, typeParameters: V8Array, optional: Boolean, modifiers: V8Array): V8Object {
        return astFactory.declareProperty(
                name,
                type.toAst(),
                typeParameters.toAst(),
                optional,
                modifiers.toAst()
        ).toV8()
    }

    fun createConstructorDeclaration(parameters: V8Array, type: V8Object, typeParameters: V8Array, modifiers: V8Array): V8Object {
        return astFactory.createConstructorDeclaration(
                parameters.toAst(),
                type.toAst(),
                typeParameters.toAst(),
                modifiers.toAst()
        ).toV8()
    }


    fun createFunctionDeclaration(name: String, parameters: V8Array, type: V8Object, typeParameters: V8Array, modifiers: V8Array, uid: String): V8Object {
        return astFactory.createFunctionDeclaration(
                name,
                parameters.toAst<ParameterDeclaration>().toTypedArray(),
                type.toAst(),
                typeParameters.toAst<TypeParameterDeclaration>().toTypedArray(),
                modifiers.toAst(),
                uid
        ).toV8()
    }

    fun createMethodSignatureDeclaration(name: String, parameters: V8Array, type: V8Object, typeParameters: V8Array, optional: Boolean, modifiers: V8Array): V8Object {
        return astFactory.createMethodSignatureDeclaration(
                name,
                parameters.toAst<ParameterDeclaration>().toTypedArray(),
                type.toAst(),
                typeParameters.toAst<TypeParameterDeclaration>().toTypedArray(),
                optional,
                modifiers.toAst()
        ).toV8()
    }


    fun createFunctionTypeDeclaration(parameters: V8Array, type: V8Object): V8Object {
        return astFactory.createFunctionTypeDeclaration(
                parameters.toAst<ParameterDeclaration>().toTypedArray(),
                type.toAst()
        ).toV8()
    }

    fun createIntersectionTypeDeclaration(params: V8Array): V8Object  = astFactory.createIntersectionTypeDeclaration(params.toAst()).toV8()
    fun createUnionTypeDeclaration(params: V8Array): V8Object  = astFactory.createUnionDeclaration(params.toAst()).toV8()
    fun createTypeDeclaration(value: String, params: V8Array): V8Object =
            astFactory.createTypeDeclaration(value, params.toAst<ParameterValueDeclaration>().toTypedArray()).toV8()

    fun createParameterDeclaration(name: String, type: V8Object, initializer: V8Object?, vararg: Boolean, optional: Boolean): V8Object =
            astFactory.createParameterDeclaration(
                    name, type.toAst(),
                    if (initializer == null) null else initializer.toAst<ExpressionDeclaration>(),
                    vararg,
                    optional
            ).toV8()

    fun createDocumentRoot(packageName: String, declarations: V8Array, modifiers: V8Array, uid: String): V8Object =
            astFactory.createDocumentRoot(
                    packageName,
                    declarations.toAst<TopLevelDeclaration>().toTypedArray(),
                    modifiers.toAst(),
                    uid
            ).toV8()

    fun createTypeParam(name: String, constraints: V8Array): V8Object = astFactory
            .createTypeParam(name, constraints.toAst<ParameterValueDeclaration>().toTypedArray()).toV8()
}
