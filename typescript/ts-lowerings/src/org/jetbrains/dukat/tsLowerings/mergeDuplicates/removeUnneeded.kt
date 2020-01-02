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

internal fun ParameterDeclaration.removeUnneeded(name: String = "") = copy(
        name = name,
        type = type.removeUnneeded()
)

internal fun CallSignatureDeclaration.normalizeDeclaration() = removeUnneeded().copy(
        type = UnionTypeDeclaration(emptyList()) //Dummy (invalid type)
)

internal fun CallSignatureDeclaration.removeUnneeded() = copy(
        parameters = parameters.map { parameter -> parameter.removeUnneeded(parameter.name) },
        type = type.removeUnneeded()
)

internal fun ConstructorDeclaration.removeUnneeded() = copy(
        parameters = parameters.map { it.removeUnneeded() }
)

internal fun MemberDeclaration.removeUnneeded() : MemberDeclaration {
    return when (this) {
        is CallSignatureDeclaration -> this.removeUnneeded()
        is ConstructorDeclaration -> this.removeUnneeded()
        is FunctionDeclaration -> this.removeUnneeded()
        else -> this
    }
}

internal fun FunctionTypeDeclaration.removeUnneeded() = copy(
        parameters = parameters.map { it.removeUnneeded() },
        type = type.removeUnneeded()
)

internal fun FunctionDeclaration.normalizeDeclaration() = removeUnneeded().copy(
        type = UnionTypeDeclaration(emptyList()) //Dummy (invalid type)
)

internal fun FunctionDeclaration.removeUnneeded() = copy(
        parameters = parameters.map { parameter -> parameter.removeUnneeded(parameter.name) },
        type = type.removeUnneeded(),
        body = null,
        uid = ""
)

internal fun UnionTypeDeclaration.removeUnneeded() = copy(
        params = params.map { it.removeUnneeded() }
)

internal fun ObjectLiteralDeclaration.removeUnneeded() = copy(
        members = members.map { it.removeUnneeded() },
        uid = ""
)

internal fun ParameterValueDeclaration.removeUnneeded() : ParameterValueDeclaration {
    return when (this) {
        is FunctionTypeDeclaration -> this.removeUnneeded()
        is ObjectLiteralDeclaration -> this.removeUnneeded()
        is UnionTypeDeclaration -> this.removeUnneeded()
        else -> this
    }
}
