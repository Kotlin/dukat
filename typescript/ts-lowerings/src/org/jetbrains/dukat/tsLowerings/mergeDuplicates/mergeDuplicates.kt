package org.jetbrains.dukat.tsLowerings.mergeDuplicates

import org.jetbrains.dukat.panic.raiseConcern
import org.jetbrains.dukat.tsmodel.CallSignatureDeclaration
import org.jetbrains.dukat.tsmodel.ClassDeclaration
import org.jetbrains.dukat.tsmodel.ConstructorDeclaration
import org.jetbrains.dukat.tsmodel.FunctionDeclaration
import org.jetbrains.dukat.tsmodel.FunctionLikeDeclaration
import org.jetbrains.dukat.tsmodel.MemberDeclaration
import org.jetbrains.dukat.tsmodel.ModuleDeclaration
import org.jetbrains.dukat.tsmodel.ParameterDeclaration
import org.jetbrains.dukat.tsmodel.PropertyDeclaration
import org.jetbrains.dukat.tsmodel.SourceFileDeclaration
import org.jetbrains.dukat.tsmodel.SourceSetDeclaration
import org.jetbrains.dukat.tsmodel.TopLevelDeclaration
import org.jetbrains.dukat.tsmodel.VariableDeclaration
import org.jetbrains.dukat.tsmodel.types.FunctionTypeDeclaration
import org.jetbrains.dukat.tsmodel.types.ObjectLiteralDeclaration
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration
import org.jetbrains.dukat.tsmodel.types.TypeDeclaration
import org.jetbrains.dukat.tsmodel.types.UnionTypeDeclaration

private object IRRELEVANT_TYPE : ParameterValueDeclaration {
    override val nullable: Boolean
        get() = raiseConcern("Irrelevant type is not supposed to be used") { false }
    override var meta: ParameterValueDeclaration?
        get() = raiseConcern("Irrelevant type is not supposed to be used") { null }
        set(_) {}
}

private fun List<FunctionTypeDeclaration>.mergeFunctionTypes() : List<ParameterValueDeclaration> {
    val minimizedFunctions = map { it.mergeDuplicates() }

    val groups = minimizedFunctions.groupBy { it.normalize() }

    return groups.map { (combination, functions) ->
        if (functions.size == 1) {
            functions[0]
        } else {
            combination
        }
    }
}

private fun List<ObjectLiteralDeclaration>.mergeObjectTypes() : List<ParameterValueDeclaration> {
    return map { it.mergeDuplicates() }.distinctBy { it.normalize() }
}

private fun List<TypeDeclaration>.mergeTypeDeclarations() : List<ParameterValueDeclaration> {
    return distinct()
}

private val UnionTypeDeclaration.flatParameters : List<ParameterValueDeclaration>
    get() {
        return params.flatMap {
            when (it) {
                is UnionTypeDeclaration -> it.flatParameters
                else -> listOf(it)
            }
        }
    }

private fun UnionTypeDeclaration.mergeUnion() : ParameterValueDeclaration {
    val types = mutableListOf<ParameterValueDeclaration>()
    val functionTypes = mutableListOf<FunctionTypeDeclaration>()
    val objectTypes = mutableListOf<ObjectLiteralDeclaration>()
    val typeDeclarations = mutableListOf<TypeDeclaration>()

    flatParameters.forEach {
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
        is ObjectLiteralDeclaration -> this.mergeDuplicates()
        is UnionTypeDeclaration -> this.mergeUnion()
        else -> this
    }
}

private fun List<FunctionLikeDeclaration>.combinedReturnType(): ParameterValueDeclaration
        = UnionTypeDeclaration(this.map { it.type }).mergeUnion()

private fun List<FunctionDeclaration>.mergeFunctions() : List<FunctionDeclaration> {
    val groups = this.groupBy { it.normalize(IRRELEVANT_TYPE) }

    return groups.map { (_, functions) ->
        if (functions.size == 1) {
            functions[0]
        } else {
            functions[0].copy(type = functions.combinedReturnType())
        }
    }
}

private fun List<CallSignatureDeclaration>.mergeCallSignatures() : List<CallSignatureDeclaration> {
    val fixedCallSignatures = map { it.mergeDuplicates() }

    val groups = fixedCallSignatures.groupBy { it.normalize(IRRELEVANT_TYPE) }

    return groups.map { (_, callSignatures) ->
        if (callSignatures.size == 1) {
            callSignatures[0]
        } else {
            callSignatures[0].copy(type = callSignatures.combinedReturnType())
        }
    }
}

private fun List<MemberDeclaration>.mergeMembers() : List<MemberDeclaration> {
    val otherMembers = mutableListOf<MemberDeclaration>()
    val constructors = mutableListOf<ConstructorDeclaration>()
    val methods = mutableListOf<FunctionDeclaration>()
    val callSignatures = mutableListOf<CallSignatureDeclaration>()

    forEach { member ->
        when (member) {
            is ConstructorDeclaration -> constructors += member.mergeDuplicates()
            is FunctionDeclaration -> methods += member.mergeDuplicates()
            is CallSignatureDeclaration -> callSignatures += member.mergeDuplicates()
            is PropertyDeclaration -> otherMembers += member.mergeDuplicates()
            else -> otherMembers += member
        }
    }

    val members = mutableListOf<MemberDeclaration>()

    members.addAll(constructors.distinct())
    members.addAll(callSignatures.mergeCallSignatures())
    members.addAll(otherMembers)
    members.addAll(methods.mergeFunctions())

    return members
}

private fun ParameterDeclaration.mergeDuplicates() = copy(
        type = type.mergeDuplicates()
)

private fun PropertyDeclaration.mergeDuplicates() = copy(
        type = type.mergeDuplicates()
)

private fun ConstructorDeclaration.mergeDuplicates() = copy(
        parameters = parameters.map { it.mergeDuplicates() }
)

private fun CallSignatureDeclaration.mergeDuplicates() = copy(
        parameters = parameters.map { it.mergeDuplicates() }
)

private fun ClassDeclaration.mergeDuplicates() = copy(
        members = members.mergeMembers()
)

private fun ObjectLiteralDeclaration.mergeDuplicates() = copy(
        members = members.mergeMembers()
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

private fun List<TopLevelDeclaration>.mergeTopLevelDeclarations() : List<TopLevelDeclaration> {
    val otherDeclarations = mutableListOf<TopLevelDeclaration>()
    val classes = mutableListOf<ClassDeclaration>()
    val functions = mutableListOf<FunctionDeclaration>()
    val variables = mutableListOf<VariableDeclaration>()

    forEach { declaration ->
        when (declaration) {
            is ClassDeclaration -> classes += declaration.mergeDuplicates()
            is FunctionDeclaration -> functions += declaration.mergeDuplicates()
            is VariableDeclaration -> variables += declaration.mergeDuplicates()
            else -> otherDeclarations += declaration
        }
    }

    val declarations = mutableListOf<TopLevelDeclaration>()

    declarations.addAll(variables.distinct())
    declarations.addAll(functions.mergeFunctions())
    declarations.addAll(classes.distinct())
    declarations.addAll(otherDeclarations)

    return declarations
}

fun ModuleDeclaration.mergeDuplicates() = copy(
        declarations = declarations.mergeTopLevelDeclarations()
)

fun SourceFileDeclaration.mergeDuplicates() = copy(
        root = root.mergeDuplicates()
)

fun SourceSetDeclaration.mergeDuplicates() = copy(
        sources = sources.map { it.mergeDuplicates() }
)