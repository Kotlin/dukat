package org.jetbrains.dukat.ast.j2v8

import com.eclipsesource.v8.V8Array
import com.eclipsesource.v8.V8Object
import com.eclipsesource.v8.utils.V8ObjectUtils
import org.jetbrains.dukat.astCommon.AstNode
import org.jetbrains.dukat.astCommon.TopLevelDeclaration
import org.jetbrains.dukat.tsmodel.ExpressionDeclaration
import org.jetbrains.dukat.tsmodel.ParameterDeclaration
import org.jetbrains.dukat.tsmodel.TypeParameterDeclaration
import org.jetbrains.dukat.tsmodel.converters.toAst
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

class AstV8Factory(private val astFactory: AstJ2V8Factory) {

    fun createExportAssignmentDeclaration(name: String) = astFactory.createExportAssignmentDeclaration(name)
    fun createTokenDeclaration(value: String) = astFactory.createTokenDeclaration(value)

    fun createHeritageClauseDeclaration(name: String, typeArguments: V8Array, extending: Boolean) = astFactory.createHeritageClauseDeclaration(
            name,
            typeArguments.toAst(),
            extending
    )

    fun createTypeAliasDeclaration(aliasName: String, typeParameters: V8Array, typeReference: V8Object) = astFactory.createTypeAliasDeclaration(
            aliasName,
            typeParameters.toAst(),
            typeReference.toAst()
    )


    fun createStringTypeDeclaration(tokens: V8Array): V8Object {
        return astFactory.createStringTypeDeclaration(
                tokens.asIterator().asSequence().map { it as String }.toList()
        )
    }

    fun createIndexSignatureDeclaration(indexType: V8Array, returnType: V8Object): V8Object {
        return astFactory.createIndexSignatureDeclaration(
                indexType.toAst(),
                returnType.toAst()
        )
    }

    fun createCallSignatureDeclaration(parameters: V8Array, type: V8Object, typeParameters: V8Array): V8Object {
        return astFactory.createCallSignatureDeclaration(
                parameters.toAst(),
                type.toAst(),
                typeParameters.toAst()
        )
    }


    fun createModifierDeclaration(token: String) = astFactory.createModifierDeclaration(token)

    fun createClassDeclaration(name: String, members: V8Array, typeParameters: V8Array, parentEntities: V8Array, modifiers: V8Array): V8Object = astFactory.createClassDeclaration(
            name,
            members.toAst(),
            typeParameters.toAst(),
            parentEntities.toAst(),
            modifiers.toAst()
    )

    fun createObjectLiteral(members: V8Array) = astFactory.createObjectLiteral(members.toAst())

    fun createInterfaceDeclaration(name: String, members: V8Array, typeParameters: V8Array, parentEntities: V8Array): V8Object = astFactory.createInterfaceDeclaration(
            name,
            members.toAst(),
            typeParameters.toAst(),
            parentEntities.toAst()
    )

    fun createExpression(kind: V8Object, meta: String) = astFactory.createExpression(kind.toAst(), meta)

    fun declareVariable(name: String, type: V8Object, modifiers: V8Array): V8Object =
            astFactory.declareVariable(
                    name,
                    type.toAst(),
                    modifiers.toAst()
            )

    fun declareProperty(name: String, type: V8Object, typeParameters: V8Array, optional: Boolean, modifiers: V8Array): V8Object {
        return astFactory.declareProperty(
                name,
                type.toAst(),
                typeParameters.toAst(),
                optional,
                modifiers.toAst()
        )
    }

    fun createConstructorDeclaration(parameters: V8Array, type: V8Object, typeParameters: V8Array, modifiers: V8Array): V8Object {
        return astFactory.createConstructorDeclaration(
                parameters.toAst(),
                type.toAst(),
                typeParameters.toAst(),
                modifiers.toAst()
        )
    }


    fun createFunctionDeclaration(name: String, parameters: V8Array, type: V8Object, typeParameters: V8Array, modifiers: V8Array): V8Object {
        return astFactory.createFunctionDeclaration(
                name,
                parameters.toAst<ParameterDeclaration>().toTypedArray(),
                type.toAst(),
                typeParameters.toAst<TypeParameterDeclaration>().toTypedArray(),
                modifiers.toAst()
        )
    }

    fun createMethodSignatureDeclaration(name: String, parameters: V8Array, type: V8Object, typeParameters: V8Array, optional: Boolean, modifiers: V8Array): V8Object {
        return astFactory.createMethodSignatureDeclaration(
                name,
                parameters.toAst<ParameterDeclaration>().toTypedArray(),
                type.toAst(),
                typeParameters.toAst<TypeParameterDeclaration>().toTypedArray(),
                optional,
                modifiers.toAst()
        )
    }


    fun createFunctionTypeDeclaration(parameters: V8Array, type: V8Object): V8Object {
        return astFactory.createFunctionTypeDeclaration(
                parameters.toAst<ParameterDeclaration>().toTypedArray(),
                type.toAst()
        )
    }

    fun createUnionTypeDeclaration(params: V8Array) = astFactory.createUnionDeclaration(params.toAst())
    fun createTypeDeclaration(value: String, params: V8Array) = astFactory.createTypeDeclaration(value, params.toAst<ParameterValueDeclaration>().toTypedArray())

    fun createParameterDeclaration(name: String, type: V8Object, initializer: V8Object?, vararg: Boolean, optional: Boolean) =
            astFactory.createParameterDeclaration(
                    name, type.toAst(),
                    if (initializer == null) null else initializer.toAst<ExpressionDeclaration>(),
                    vararg,
                    optional
            )

    fun createDocumentRoot(packageName: String, declarations: V8Array, modifiers: V8Array) =
            astFactory.createDocumentRoot(
                    packageName,
                    declarations.toAst<TopLevelDeclaration>().toTypedArray(),
                    modifiers.toAst()
            )

    fun createTypeParam(name: String, constraints: V8Array) = astFactory
            .createTypeParam(name, constraints.toAst<ParameterValueDeclaration>().toTypedArray())
}
