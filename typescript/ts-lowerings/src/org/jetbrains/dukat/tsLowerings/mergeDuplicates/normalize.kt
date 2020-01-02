package org.jetbrains.dukat.tsLowerings.mergeDuplicates

import org.jetbrains.dukat.tsmodel.CallSignatureDeclaration
import org.jetbrains.dukat.tsmodel.ConstructorDeclaration
import org.jetbrains.dukat.tsmodel.FunctionDeclaration
import org.jetbrains.dukat.tsmodel.MemberDeclaration
import org.jetbrains.dukat.tsmodel.ParameterDeclaration
import org.jetbrains.dukat.tsmodel.types.FunctionTypeDeclaration
import org.jetbrains.dukat.tsmodel.types.ObjectLiteralDeclaration
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration
import org.jetbrains.dukat.tsmodel.types.UnionTypeDeclaration

const val IRRELEVANT_UID = "<IRRELEVANT>"

private object IRRELEVANT_TYPE : ParameterValueDeclaration {
    override val nullable: Boolean
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
    override var meta: ParameterValueDeclaration?
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
        set(_) {}
}

internal fun ParameterDeclaration.normalize(name: String = "") = copy(
        name = name,
        type = type.normalize()
)

internal fun CallSignatureDeclaration.normalizeDeclaration() = normalize().copy(
        type = IRRELEVANT_TYPE
)

internal fun CallSignatureDeclaration.normalize() = copy(
        parameters = parameters.map { parameter -> parameter.normalize(parameter.name) },
        type = type.normalize()
)

internal fun ConstructorDeclaration.normalize() = copy(
        parameters = parameters.map { it.normalize() }
)

internal fun MemberDeclaration.normalize() : MemberDeclaration {
    return when (this) {
        is CallSignatureDeclaration -> this.normalize()
        is ConstructorDeclaration -> this.normalize()
        is FunctionDeclaration -> this.normalize()
        else -> this
    }
}

internal fun FunctionTypeDeclaration.normalize() = copy(
        parameters = parameters.map { it.normalize() },
        type = type.normalize()
)

internal fun FunctionDeclaration.normalizeDeclaration() = normalize().copy(
        type = IRRELEVANT_TYPE
)

internal fun FunctionDeclaration.normalize() = copy(
        parameters = parameters.map { parameter -> parameter.normalize(parameter.name) },
        type = type.normalize(),
        body = null,
        uid = IRRELEVANT_UID
)

internal fun UnionTypeDeclaration.normalize() = copy(
        params = params.map { it.normalize() }
)

internal fun ObjectLiteralDeclaration.normalize() = copy(
        members = members.map { it.normalize() },
        uid = IRRELEVANT_UID
)

internal fun ParameterValueDeclaration.normalize() : ParameterValueDeclaration {
    return when (this) {
        is FunctionTypeDeclaration -> this.normalize()
        is ObjectLiteralDeclaration -> this.normalize()
        is UnionTypeDeclaration -> this.normalize()
        else -> this
    }
}
