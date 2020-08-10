package org.jetbrains.dukat.tsLowerings

import TopLevelDeclarationLowering
import cartesian
import org.jetbrains.dukat.astCommon.Entity
import org.jetbrains.dukat.ownerContext.NodeOwner
import org.jetbrains.dukat.panic.raiseConcern
import org.jetbrains.dukat.tsmodel.CallSignatureDeclaration
import org.jetbrains.dukat.tsmodel.ClassDeclaration
import org.jetbrains.dukat.tsmodel.ConstructorDeclaration
import org.jetbrains.dukat.tsmodel.EnumDeclaration
import org.jetbrains.dukat.tsmodel.ExpressionStatementDeclaration
import org.jetbrains.dukat.tsmodel.FunctionDeclaration
import org.jetbrains.dukat.tsmodel.GeneratedInterfaceDeclaration
import org.jetbrains.dukat.tsmodel.GeneratedInterfaceReferenceDeclaration
import org.jetbrains.dukat.tsmodel.ImportEqualsDeclaration
import org.jetbrains.dukat.tsmodel.InterfaceDeclaration
import org.jetbrains.dukat.tsmodel.CallableMemberDeclaration
import org.jetbrains.dukat.tsmodel.MethodDeclaration
import org.jetbrains.dukat.tsmodel.MethodSignatureDeclaration
import org.jetbrains.dukat.tsmodel.ModuleDeclaration
import org.jetbrains.dukat.tsmodel.ParameterDeclaration
import org.jetbrains.dukat.tsmodel.SourceSetDeclaration
import org.jetbrains.dukat.tsmodel.TopLevelDeclaration
import org.jetbrains.dukat.tsmodel.TypeAliasDeclaration
import org.jetbrains.dukat.tsmodel.VariableDeclaration
import org.jetbrains.dukat.tsmodel.types.FunctionTypeDeclaration
import org.jetbrains.dukat.tsmodel.types.IndexSignatureDeclaration
import org.jetbrains.dukat.tsmodel.types.IntersectionTypeDeclaration
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration
import org.jetbrains.dukat.tsmodel.types.StringLiteralDeclaration
import org.jetbrains.dukat.tsmodel.types.TypeDeclaration
import org.jetbrains.dukat.tsmodel.types.TypeParamReferenceDeclaration
import org.jetbrains.dukat.tsmodel.types.UnionTypeDeclaration

const val COMPLEXITY_THRESHOLD = 15

@Suppress("UNCHECKED_CAST")
private fun <T : Entity> Entity.duplicate(): T {
    return when (this) {
        is ClassDeclaration -> copy() as T
        is EnumDeclaration -> copy() as T
        is ExpressionStatementDeclaration -> copy() as T
        is FunctionDeclaration -> copy() as T
        is FunctionTypeDeclaration -> copy() as T
        is GeneratedInterfaceReferenceDeclaration -> copy() as T
        is ImportEqualsDeclaration -> copy() as T
        is InterfaceDeclaration -> copy() as T
        is IntersectionTypeDeclaration -> copy() as T
        is ModuleDeclaration -> copy() as T
        is ParameterDeclaration -> copy() as T
        is StringLiteralDeclaration -> copy() as T
        is TypeDeclaration -> copy() as T
        is TypeParamReferenceDeclaration -> copy() as T
        is UnionTypeDeclaration -> copy() as T
        is VariableDeclaration -> copy() as T

        else -> raiseConcern("can not copy ${this}") { this as T }
    }
}

private fun CallableMemberDeclaration.copyWithParams(params: List<ParameterDeclaration>): CallableMemberDeclaration {
    return when (this) {
        is ConstructorDeclaration -> copy(parameters = params)
        is MethodSignatureDeclaration -> copy(parameters = params)
        is CallSignatureDeclaration -> copy(parameters = params)
        is IndexSignatureDeclaration -> copy(parameters = params)
        is MethodDeclaration -> copy(parameters = params)
        else -> raiseConcern("can not update params for ${this}") { this }
    }
}

private fun specifyArguments(params: List<ParameterDeclaration>): List<List<ParameterDeclaration>> {

    var currentComplexity = 1

    return params.map { parameterDeclaration ->
        when (val type = parameterDeclaration.type) {
            is UnionTypeDeclaration -> {
                currentComplexity *= type.params.size
                if (currentComplexity <= COMPLEXITY_THRESHOLD) {
                    type.params.map { param ->
                        parameterDeclaration.copy(type = param)
                    }
                } else {
                    listOf(parameterDeclaration)
                }
            }
            else -> {
                listOf(parameterDeclaration)
            }
        }
    }
}

private class SpecifyUnionTypeLowering(private val generatedMethodsMap: MutableMap<String, MutableList<CallableMemberDeclaration>>) : TopLevelDeclarationLowering {

    fun generateParams(params: List<ParameterDeclaration>): Pair<List<List<ParameterDeclaration>>, Boolean> {
        val specifyParams = specifyArguments(params)
        val hasUnrolledParams = specifyParams.any { it.size > 1 }

        return if (specifyParams.size == 1) {
            Pair(specifyParams.first().map { param -> listOf(param) }, hasUnrolledParams)
        } else {
            Pair(cartesian(*specifyParams.toTypedArray()), hasUnrolledParams)
        }
    }

    fun generateMethods(declaration: CallableMemberDeclaration, uid: String?): List<CallableMemberDeclaration> {
        val generatedParams = generateParams(declaration.parameters)
        return generatedParams.first.map { params ->
            val declarationResolved = declaration.copyWithParams(params)
            if (generatedParams.second && (uid != null)) {
                generatedMethodsMap.getOrPut(uid) { mutableListOf() }.add(declarationResolved)
            }

            declarationResolved
        }
    }

    private fun ConstructorDeclaration.asKey() = parameters.map { param ->
        param.type.duplicate<ParameterValueDeclaration>().let {
            it.meta = null
            it
        }
    }

    private fun FunctionDeclaration.asKey() = parameters.map { param ->
        param.type.duplicate<ParameterValueDeclaration>().let {
            it.meta = null
            it
        }
    }

    fun generateFunctionNodes(declaration: FunctionDeclaration): List<FunctionDeclaration> {
        return generateParams(declaration.parameters).first.map { params ->
            declaration.copy(parameters = params)
        }.distinctBy { node -> node.asKey() }
    }

    fun generateConstructors(declaration: ConstructorDeclaration): List<ConstructorDeclaration> {
        return generateParams(declaration.parameters).first.map { params ->
            declaration.copy(parameters = params)
        }.distinctBy { node -> node.asKey() }
    }

    override fun lowerClassDeclaration(declaration: ClassDeclaration, owner: NodeOwner<ModuleDeclaration>?): TopLevelDeclaration {
        val members = declaration.members.flatMap { member ->
            when (member) {
                is ConstructorDeclaration -> generateConstructors(member)
                is CallableMemberDeclaration -> generateMethods(member, null)
                else -> listOf(member)
            }
        }
        return declaration.copy(members = members)
    }

    override fun lowerGeneratedInterfaceDeclaration(declaration: GeneratedInterfaceDeclaration, owner: NodeOwner<ModuleDeclaration>?): GeneratedInterfaceDeclaration {
        val members = declaration.members.flatMap { member ->
            when (member) {
                is CallableMemberDeclaration -> generateMethods(member, declaration.uid)
                else -> listOf(member)
            }
        }
        return declaration.copy(members = members)
    }

    override fun lowerInterfaceDeclaration(declaration: InterfaceDeclaration, owner: NodeOwner<ModuleDeclaration>?): TopLevelDeclaration {
        val members = declaration.members.flatMap { member ->
            when (member) {
                is CallableMemberDeclaration -> generateMethods(member, declaration.uid)
                else -> listOf(member)
            }
        }
        return declaration.copy(members = members)
    }

    fun lowerTopLevelDeclarationList(declaration: TopLevelDeclaration, owner: NodeOwner<ModuleDeclaration>?): List<TopLevelDeclaration> {
        return when (declaration) {
            is VariableDeclaration -> listOf(lowerVariableDeclaration(declaration, owner))
            is FunctionDeclaration -> generateFunctionNodes(declaration)
            is ClassDeclaration -> listOf(lowerClassDeclaration(declaration, owner))
            is InterfaceDeclaration -> listOf(lowerInterfaceDeclaration(declaration, owner))
            is GeneratedInterfaceDeclaration -> listOf(lowerGeneratedInterfaceDeclaration(declaration, owner))
            is ModuleDeclaration -> listOf(lowerSourceDeclaration(declaration, owner))
            is TypeAliasDeclaration -> listOf(lowerTypeAliasDeclaration(declaration, owner))
            else -> listOf(declaration)
        }
    }

    override fun lowerTopLevelDeclarations(declarations: List<TopLevelDeclaration>, owner: NodeOwner<ModuleDeclaration>?): List<TopLevelDeclaration> {
        return declarations.flatMap { declaration ->
            lowerTopLevelDeclarationList(declaration, owner)
        }
    }
}

private class IntroduceGeneratedMembersToDescendantClasses(private val generatedMembersMap: Map<String, List<CallableMemberDeclaration>>) : TopLevelDeclarationLowering {
    override fun lowerClassDeclaration(declaration: ClassDeclaration, owner: NodeOwner<ModuleDeclaration>?): TopLevelDeclaration? {
        val generatedMembers = declaration.parentEntities.mapNotNull { generatedMembersMap[it.typeReference?.uid] }.flatten()
        return super.lowerClassDeclaration(declaration.copy(members = declaration.members + generatedMembers), owner)

    }
}

class SpecifyUnionType : TsLowering {
    override fun lower(source: SourceSetDeclaration): SourceSetDeclaration {
        val generatedMethodsMap = mutableMapOf<String, MutableList<CallableMemberDeclaration>>()

        val unrolledSourceSet = source.copy(sources = source.sources.map { sourceFile ->
            sourceFile.copy(root = SpecifyUnionTypeLowering(generatedMethodsMap).lowerSourceDeclaration(sourceFile.root))
        })

        return unrolledSourceSet.copy(sources = unrolledSourceSet.sources.map { sourceFile ->
            sourceFile.copy(root = IntroduceGeneratedMembersToDescendantClasses(generatedMethodsMap).lowerSourceDeclaration(sourceFile.root))
        })
    }
}