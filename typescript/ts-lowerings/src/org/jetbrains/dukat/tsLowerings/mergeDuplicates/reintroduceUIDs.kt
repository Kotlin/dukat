package org.jetbrains.dukat.tsLowerings.mergeDuplicates

import org.jetbrains.dukat.tsmodel.CallSignatureDeclaration
import org.jetbrains.dukat.tsmodel.FunctionDeclaration
import org.jetbrains.dukat.tsmodel.MemberDeclaration
import org.jetbrains.dukat.tsmodel.ParameterDeclaration
import org.jetbrains.dukat.tsmodel.TypeParameterDeclaration
import org.jetbrains.dukat.tsmodel.types.FunctionTypeDeclaration
import org.jetbrains.dukat.tsmodel.types.ObjectLiteralDeclaration
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration
import org.jetbrains.dukat.tsmodel.types.UnionTypeDeclaration

private fun ParameterDeclaration.reintroduceUIDs() = copy(
        type = type.reintroduceUIDs()
)

private fun FunctionTypeDeclaration.reintroduceUIDs() = copy(
        type = type.reintroduceUIDs(),
        parameters = parameters.map { it.reintroduceUIDs() }
)

private fun ObjectLiteralDeclaration.reintroduceUIDs() = copy(
        members = members.map { it.reintroduceUIDs() },
        uid = getNewUID()
)

internal fun FunctionDeclaration.reintroduceUIDs(uid: String = getNewUID()) = copy(
        type = type.reintroduceUIDs(),
        parameters = parameters.map { it.reintroduceUIDs() },
        uid = uid
)

internal fun CallSignatureDeclaration.reintroduceUIDs() = copy(
        type = type.reintroduceUIDs(),
        parameters = parameters.map { it.reintroduceUIDs() }
)

private fun UnionTypeDeclaration.reintroduceUIDs() = copy(
        params = params.map { it.reintroduceUIDs() }
)

internal fun MemberDeclaration.reintroduceUIDs(): MemberDeclaration {
    return when (this) {
        is FunctionDeclaration -> this.reintroduceUIDs()
        is ObjectLiteralDeclaration -> this.reintroduceUIDs()
        is CallSignatureDeclaration -> this.reintroduceUIDs()
        else -> this
    }
}

internal fun ParameterValueDeclaration.reintroduceUIDs(): ParameterValueDeclaration {
    return when (this) {
        is FunctionTypeDeclaration -> this.reintroduceUIDs()
        is ObjectLiteralDeclaration -> this.reintroduceUIDs()
        is UnionTypeDeclaration -> this.reintroduceUIDs()
        else -> this
    }
}