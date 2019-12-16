package org.jetbrains.dukat.tsLowerings

import org.jetbrains.dukat.tsmodel.CallSignatureDeclaration
import org.jetbrains.dukat.tsmodel.ClassDeclaration
import org.jetbrains.dukat.tsmodel.ConstructorDeclaration
import org.jetbrains.dukat.tsmodel.FunctionDeclaration
import org.jetbrains.dukat.tsmodel.MemberDeclaration
import org.jetbrains.dukat.tsmodel.ModuleDeclaration
import org.jetbrains.dukat.tsmodel.ParameterDeclaration
import org.jetbrains.dukat.tsmodel.SourceFileDeclaration
import org.jetbrains.dukat.tsmodel.SourceSetDeclaration
import org.jetbrains.dukat.tsmodel.TopLevelDeclaration
import org.jetbrains.dukat.tsmodel.VariableDeclaration
import org.jetbrains.dukat.tsmodel.types.FunctionTypeDeclaration
import org.jetbrains.dukat.tsmodel.types.ObjectLiteralDeclaration
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration
import org.jetbrains.dukat.tsmodel.types.TypeDeclaration
import org.jetbrains.dukat.tsmodel.types.UnionTypeDeclaration

private fun FunctionTypeDeclaration.removeParameterNames() = copy(
        parameters = parameters.map { it.copy(name = "") }
)

private fun List<FunctionTypeDeclaration>.mergeFunctionTypes() : List<ParameterValueDeclaration> {
    val groups = groupBy { it.removeParameterNames() }

    return groups.map { (combination, functions) ->
        if (functions.size == 1) {
            functions[0]
        } else {
            //TODO check if the names are all the same, in which case removing them isn't necessary
            combination
        }
    }
}

private fun List<ObjectLiteralDeclaration>.mergeObjectTypes() : List<ParameterValueDeclaration> {
    return toSet().toList() //TODO check if this is enough
}

private fun List<TypeDeclaration>.mergeTypeDeclarations() : List<ParameterValueDeclaration> {
    return toSet().toList()
}

private fun UnionTypeDeclaration.mergeUnion() : ParameterValueDeclaration {
    val types = mutableListOf<ParameterValueDeclaration>()
    val functionTypes = mutableListOf<FunctionTypeDeclaration>()
    val objectTypes = mutableListOf<ObjectLiteralDeclaration>()
    val typeDeclarations = mutableListOf<TypeDeclaration>()

    params.forEach {
        when (it) {
            is FunctionTypeDeclaration -> functionTypes += it.mergeUnions()
            is ObjectLiteralDeclaration -> objectTypes += it
            is TypeDeclaration -> typeDeclarations += it
            else -> types += it.mergeUnions()
        }
    }

    types.addAll(functionTypes.mergeFunctionTypes())
    types.addAll(objectTypes.mergeObjectTypes())
    types.addAll(typeDeclarations.mergeTypeDeclarations())

    return when (types.size) {
        1 -> types[0]
        else -> UnionTypeDeclaration(types)
    }
}

private fun ParameterValueDeclaration.mergeUnions() : ParameterValueDeclaration {
    return when (this) {
        is FunctionTypeDeclaration -> this.mergeUnions()
        is UnionTypeDeclaration -> this.mergeUnion()
        else -> this
    }
}

private fun ParameterDeclaration.mergeUnions() = copy(
        type = type.mergeUnions()
)

private fun ConstructorDeclaration.mergeUnions() = copy(
        parameters = parameters.map { it.mergeUnions() }
)

private fun CallSignatureDeclaration.mergeUnions() = copy(
        parameters = parameters.map { it.mergeUnions() }
)

private fun MemberDeclaration.mergeUnions() : MemberDeclaration {
    return when (this) {
        is ConstructorDeclaration -> this.mergeUnions()
        is FunctionDeclaration -> this.mergeUnions()
        is CallSignatureDeclaration -> this.mergeUnions()
        else -> this
    }
}

private fun ClassDeclaration.mergeUnions() = copy(
        members = members.map { it.mergeUnions() }
)

private fun FunctionTypeDeclaration.mergeUnions() = copy(
        parameters = parameters.map { it.mergeUnions() },
        type = type.mergeUnions()
)

private fun FunctionDeclaration.mergeUnions() = copy(
        parameters = parameters.map { it.mergeUnions() },
        type = type.mergeUnions()
)

private fun VariableDeclaration.mergeUnions() = copy(
        type = type.mergeUnions()
)

private fun TopLevelDeclaration.mergeUnions() : TopLevelDeclaration {
    return when (this) {
        is ClassDeclaration -> this.mergeUnions()
        is FunctionDeclaration -> this.mergeUnions()
        is VariableDeclaration -> this.mergeUnions()
        else -> this
    }
}

fun ModuleDeclaration.mergeUnions() = copy(
        declarations = declarations.map { it.mergeUnions() }
)

fun SourceFileDeclaration.mergeUnions() = copy(
        root = root.mergeUnions()
)

fun SourceSetDeclaration.mergeUnions() = copy(
        sources = sources.map { it.mergeUnions() }
)