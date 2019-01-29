package org.jetbrains.dukat.ast.j2v8

import com.eclipsesource.v8.V8Array
import com.eclipsesource.v8.V8Object
import com.eclipsesource.v8.utils.V8ObjectUtils
import org.jetbrains.dukat.ast.model.AstNode
import org.jetbrains.dukat.ast.model.declaration.ExpressionDeclaration
import org.jetbrains.dukat.ast.model.declaration.HeritageClauseDeclaration
import org.jetbrains.dukat.ast.model.declaration.MemberDeclaration
import org.jetbrains.dukat.ast.model.declaration.ModifierDeclaration
import org.jetbrains.dukat.ast.model.declaration.ParameterDeclaration
import org.jetbrains.dukat.ast.model.declaration.TokenDeclaration
import org.jetbrains.dukat.ast.model.declaration.TypeParameterDeclaration
import org.jetbrains.dukat.ast.model.declaration.types.ParameterValueDeclaration
import org.jetbrains.dukat.ast.model.declaration.types.TopLevelDeclaration
import org.jetbrains.dukat.ast.toAst

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


class AstV8Factory(private val astFactory: AstJ2V8Factory) {

    fun createTokenDeclaration(value: String) = astFactory.createTokenDeclaration(value)

    fun createHeritageClauseDeclaration(name: String, typeArguments: V8Array, extending: Boolean) = astFactory.createHeritageClauseDeclaration(
            name,
            typeArguments.toArray().map { it.toAst<TokenDeclaration>() },
            extending
    )

    fun createTypeAliasDeclaration(aliasName: String, typeParameters: V8Array, typeReference: V8Object) = astFactory.createTypeAliasDeclaration(
            aliasName,
            typeParameters.toArray().map { typeParameter -> typeParameter.toAst<TokenDeclaration>() },
            typeReference.toAst()
    )


    fun createStringTypeDeclaration(tokens: V8Array): V8Object {
        return astFactory.createStringTypeDeclaration(
                tokens.asIterator().asSequence().map { it as String }.toList()
        )
    }

    fun createIndexSignatureDeclaration(indexType: V8Array, returnType: V8Object): V8Object {
        return astFactory.createIndexSignatureDeclaration(
                indexType.toArray().map { it.toAst<ParameterDeclaration>() },
                returnType.toAst()
        )
    }

    fun createCallSignatureDeclaration(parameters: V8Array, type: V8Object, typeParameters: V8Array): V8Object {
        val params = parameters.toArray().map { it.toAst<ParameterDeclaration>() }
        val typeParams = typeParameters.toArray().map { it.toAst<TypeParameterDeclaration>() }

        return astFactory.createCallSignatureDeclaration(
                params,
                type.toAst(),
                typeParams
        )
    }


    fun createModifierDeclaration(token: String) = astFactory.createModifierDeclaration(token)

    fun createClassDeclaration(name: String, members: V8Array, typeParameters: V8Array, parentEntities: V8Array, staticMembers: V8Array): V8Object = astFactory.createClassDeclaration(
            name,
            members.toArray().map { member -> member.toAst<MemberDeclaration>() },
            typeParameters.toArray().map { typeParameter -> typeParameter.toAst<TypeParameterDeclaration>() },
            parentEntities.toArray().map { typeParameter -> typeParameter.toAst<HeritageClauseDeclaration>() },
            staticMembers.toArray().map { staticMember -> staticMember.toAst<MemberDeclaration>() }
    )

    fun createObjectLiteral(members: V8Array) = astFactory.createObjectLiteral(members.toArray().map { method -> method.toAst<MemberDeclaration>() })

    fun createInterfaceDeclaration(name: String, members: V8Array, typeParameters: V8Array, parentEntities: V8Array): V8Object = astFactory.createInterfaceDeclaration(
            name,
            members.toArray().map { method -> method.toAst<MemberDeclaration>() },
            typeParameters.toArray().map { typeParameter -> typeParameter.toAst<TypeParameterDeclaration>() },
            parentEntities.toArray().map { parentEntity -> parentEntity.toAst<HeritageClauseDeclaration>() }
    )

    fun createExpression(kind: V8Object, meta: String) = astFactory.createExpression(kind.toAst(), meta)

    fun declareVariable(name: String, type: V8Object): V8Object = astFactory.declareVariable(name, type.toAst())
    fun declareProperty(name: String, type: V8Object, typeParameters: V8Array, optional: Boolean, modifiers: V8Array): V8Object {
        return astFactory.declareProperty(
                name,
                type.toAst(),
                typeParameters.toArray().map { it.toAst<TypeParameterDeclaration>() },
                optional,
                modifiers.toArray().map { it.toAst<ModifierDeclaration>() }
        )
    }

    fun createConstructorDeclaration(parameters: V8Array, type: V8Object, typeParameters: V8Array, modifiers: V8Array): V8Object {
        val params = parameters.toArray().map { it.toAst<ParameterDeclaration>() }
        val typeParams = typeParameters.toArray().map { it.toAst<TypeParameterDeclaration>() }
        val modifiersParams = modifiers.toArray().map { it.toAst<ModifierDeclaration>() }

        return astFactory.createConstructorDeclaration(
                params,
                type.toAst(),
                typeParams,
                modifiersParams
        )
    }


    fun createFunctionDeclaration(name: String, parameters: V8Array, type: V8Object, typeParameters: V8Array, modifiers: V8Array): V8Object {
        val params = parameters.toArray().map { it.toAst<ParameterDeclaration>() }.toTypedArray()
        val typeParams = typeParameters.toArray().map { it.toAst<TypeParameterDeclaration>() }.toTypedArray()
        val modifiersParams = modifiers.toArray().map { it.toAst<ModifierDeclaration>() }

        return astFactory.createFunctionDeclaration(
                name,
                params,
                type.toAst(),
                typeParams,
                modifiersParams
        )
    }

    fun createMethodSignatureDeclaration(name: String, parameters: V8Array, type: V8Object, typeParameters: V8Array, optional: Boolean, modifiers: V8Array): V8Object {
        val params = parameters.toArray().map { it.toAst<ParameterDeclaration>() }.toTypedArray()
        val typeParams = typeParameters.toArray().map { it.toAst<TypeParameterDeclaration>() }.toTypedArray()
        val modifiersParams = modifiers.toArray().map { it.toAst<ModifierDeclaration>() }

        return astFactory.createMethodSignatureDeclaration(
                name,
                params,
                type.toAst(),
                typeParams,
                optional,
                modifiersParams
        )
    }


    fun createFunctionTypeDeclaration(parameters: V8Array, type: V8Object): V8Object {
        val res = parameters.toArray().map { it.toAst<ParameterDeclaration>() }

        return astFactory.createFunctionTypeDeclaration(
                res.toTypedArray(),
                type.toAst()
        )
    }

    fun createUnionDeclaration(name: String, params: V8Array) = astFactory.createUnionDeclaration(name, params.toArray().map { type -> type.toAst<ParameterValueDeclaration>() })
    fun createTypeDeclaration(value: String, params: V8Array) = astFactory.createTypeDeclaration(value, params.toArray().map { type -> type.toAst<ParameterValueDeclaration>() }.toTypedArray())

    fun createParameterDeclaration(name: String, type: V8Object, initializer: V8Object?, vararg: Boolean) =
            astFactory.createParameterDeclaration(
                    name, type.toAst(),
                    if (initializer == null) null else initializer.toAst<ExpressionDeclaration>(),
                    vararg
            )

    fun createDocumentRoot(packageName: String, declarations: V8Array) =
            astFactory.createDocumentRoot(packageName, declarations.toArray().map { declaration -> declaration.toAst<TopLevelDeclaration>() }.toTypedArray())

    fun createTypeParam(name: String, constraints: V8Array) = astFactory
            .createTypeParam(name, constraints.toArray()
                    .map { constraint -> constraint.toAst<ParameterValueDeclaration>() }.toTypedArray())
}
