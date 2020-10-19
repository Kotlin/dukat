package org.jetbrains.dukat.tsLowerings

import org.jetbrains.dukat.ownerContext.NodeOwner
import org.jetbrains.dukat.tsmodel.ClassLikeDeclaration
import org.jetbrains.dukat.tsmodel.GeneratedInterfaceReferenceDeclaration
import org.jetbrains.dukat.tsmodel.MemberDeclaration
import org.jetbrains.dukat.tsmodel.ModifierDeclaration
import org.jetbrains.dukat.tsmodel.ModuleDeclaration
import org.jetbrains.dukat.tsmodel.NamedMethodLikeDeclaration
import org.jetbrains.dukat.tsmodel.PropertyDeclaration
import org.jetbrains.dukat.tsmodel.SourceSetDeclaration
import org.jetbrains.dukat.tsmodel.TopLevelDeclaration
import org.jetbrains.dukat.tsmodel.copy
import org.jetbrains.dukat.tsmodel.types.FunctionTypeDeclaration
import org.jetbrains.dukat.tsmodel.types.TypeDeclaration
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

private class ProcessOptionalMethodsLowering(private val context: GeneratedInterfacesContext) : DeclarationLowering {

    @OptIn(ExperimentalContracts::class)
    private fun shouldBeReplaced(member: MemberDeclaration): Boolean {
        contract {
            returns(true) implies (member is NamedMethodLikeDeclaration)
        }
        return member is NamedMethodLikeDeclaration && member.optional
    }

    private fun createFunInterface(methods: List<NamedMethodLikeDeclaration>, ownerUid: String): GeneratedInterfaceReferenceDeclaration {
        return context.registerFunInterface(methods, ownerUid)
    }

    private fun convertOptionalMethodWithoutOverloads(method: NamedMethodLikeDeclaration): PropertyDeclaration {
        return PropertyDeclaration(
            name = method.name,
            type = FunctionTypeDeclaration(
                parameters = method.parameters,
                type = method.type,
                nullable = true
            ),
            typeParameters = method.typeParameters,
            optional = false,
            modifiers = method.modifiers + ModifierDeclaration.SYNTH_IMMUTABLE,
            explicitlyDeclaredType = true
        )
    }

    private fun convertOptionalMethodWithOverloads(
        method: NamedMethodLikeDeclaration,
        reference: GeneratedInterfaceReferenceDeclaration
    ): PropertyDeclaration {

        return PropertyDeclaration(
            name = method.name,
            type = TypeDeclaration(
                value = reference.name,
                params = reference.typeParameters,
                typeReference = reference.typeReference,
                nullable = true
            ),
            typeParameters = method.typeParameters,
            optional = true,
            modifiers = method.modifiers + ModifierDeclaration.SYNTH_IMMUTABLE,
            explicitlyDeclaredType = true
        )
    }

    override fun lowerClassLikeDeclaration(
        declaration: ClassLikeDeclaration,
        owner: NodeOwner<ModuleDeclaration>?
    ): TopLevelDeclaration? {
        val methodMap = mutableMapOf<String, MutableList<NamedMethodLikeDeclaration>>()
        declaration.members.forEach { member ->
            if (shouldBeReplaced(member)) {
                methodMap.getOrPut(member.name) { mutableListOf() } += member
            }
        }

        val localGeneratedInterfacesMap = mutableMapOf<String, GeneratedInterfaceReferenceDeclaration>()

        methodMap.forEach { (name, methods) ->
            if (methods.size > 1) {
                val funInterface = createFunInterface(methods, declaration.uid)
                localGeneratedInterfacesMap[name] = funInterface
            }
        }

        val newMembers = declaration.members.map { member ->
            if (shouldBeReplaced(member)) {
                if (methodMap[member.name]?.size == 1) {
                    convertOptionalMethodWithoutOverloads(member)
                } else {
                    convertOptionalMethodWithOverloads(member, localGeneratedInterfacesMap.getValue(member.name))
                }
            } else {
                member
            }
        }
        return declaration.copy(newMembers = newMembers)
    }

}

class ProcessOptionalMethods: TsLowering {

    val context = GeneratedInterfacesContext()

    override fun lower(source: SourceSetDeclaration): SourceSetDeclaration {
        return source.copy(
            sources = source.sources.map {
                it.copy(
                    root = context.introduceGeneratedEntities(
                        ProcessOptionalMethodsLowering(context).lowerModuleModel(it.root, NodeOwner(it.root, null))!!
                    )
                )
            }
        )
    }
}