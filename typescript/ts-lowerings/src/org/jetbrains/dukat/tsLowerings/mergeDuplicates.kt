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
    val fixedFunctions = map { it.mergeDuplicates() }

    val groups = fixedFunctions.groupBy { it.removeParameterNames() }

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
            is FunctionTypeDeclaration -> functionTypes += it
            is ObjectLiteralDeclaration -> objectTypes += it
            is TypeDeclaration -> typeDeclarations += it
            else -> types += it.mergeDuplicates()
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

private fun ParameterValueDeclaration.mergeDuplicates() : ParameterValueDeclaration {
    return when (this) {
        is FunctionTypeDeclaration -> this.mergeDuplicates()
        is UnionTypeDeclaration -> this.mergeUnion()
        else -> this
    }
}

private fun ParameterDeclaration.mergeDuplicates() = copy(
        type = type.mergeDuplicates()
)

private fun ConstructorDeclaration.mergeDuplicates() = copy(
        parameters = parameters.map { it.mergeDuplicates() }
)

private fun CallSignatureDeclaration.mergeDuplicates() = copy(
        parameters = parameters.map { it.mergeDuplicates() }
)

private fun MemberDeclaration.mergeDuplicates() : MemberDeclaration {
    return when (this) {
        is ConstructorDeclaration -> this.mergeDuplicates()
        is FunctionDeclaration -> this.mergeDuplicates()
        is CallSignatureDeclaration -> this.mergeDuplicates()
        else -> this
    }
}

private fun ClassDeclaration.mergeDuplicates() = copy(
        members = members.map { it.mergeDuplicates() }
)

private fun FunctionTypeDeclaration.mergeDuplicates() = copy(
        parameters = parameters.map { it.mergeDuplicates() },
        type = type.mergeDuplicates()
)

private fun FunctionDeclaration.mergeDuplicates() = copy(
        parameters = parameters.map { it.mergeDuplicates() },
        type = type.mergeDuplicates()
)

private fun VariableDeclaration.mergeDuplicates() = copy(
        type = type.mergeDuplicates()
)

private fun TopLevelDeclaration.mergeDuplicates() : TopLevelDeclaration {
    return when (this) {
        is ClassDeclaration -> this.mergeDuplicates()
        is FunctionDeclaration -> this.mergeDuplicates()
        is VariableDeclaration -> this.mergeDuplicates()
        else -> this
    }
}

fun ModuleDeclaration.mergeDuplicates() = copy(
        declarations = declarations.map { it.mergeDuplicates() }
)

fun SourceFileDeclaration.mergeDuplicates() = copy(
        root = root.mergeDuplicates()
)

fun SourceSetDeclaration.mergeDuplicates() = copy(
        sources = sources.map { it.mergeDuplicates() }
)