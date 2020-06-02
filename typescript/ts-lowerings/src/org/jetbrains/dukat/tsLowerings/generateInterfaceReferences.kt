package org.jetbrains.dukat.tsLowerings

import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.ownerContext.NodeOwner
import org.jetbrains.dukat.tsmodel.ModuleDeclaration
import org.jetbrains.dukat.tsmodel.ParameterOwnerDeclaration
import org.jetbrains.dukat.tsmodel.SourceFileDeclaration
import org.jetbrains.dukat.tsmodel.SourceSetDeclaration
import org.jetbrains.dukat.tsmodel.TopLevelDeclaration
import org.jetbrains.dukat.tsmodel.TypeAliasDeclaration
import org.jetbrains.dukat.tsmodel.VariableDeclaration
import org.jetbrains.dukat.tsmodel.WithUidDeclaration
import org.jetbrains.dukat.tsmodel.types.ObjectLiteralDeclaration
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration
import org.jetbrains.dukat.tsmodel.types.TypeDeclaration
import org.jetbrains.dukat.tsmodel.types.canBeJson

private class GenerateInterfaceReferencesDeclarationLowering : DeclarationLowering {

    private fun NodeOwner<*>.topmostEntity(): WithUidDeclaration? {
        val topOwner = generateSequence(this) {
            it.owner
        }.lastOrNull { (it.node is TopLevelDeclaration) && (it.node is WithUidDeclaration) && (it.node !is ModuleDeclaration) }

        return (topOwner?.node as? WithUidDeclaration)
    }

    private val myAstContext: GeneratedInterfacesContext = GeneratedInterfacesContext()

    override fun lowerObjectLiteralDeclaration(declaration: ObjectLiteralDeclaration, owner: NodeOwner<ParameterOwnerDeclaration>?): ParameterValueDeclaration {
        return when {
            declaration.canBeJson() -> TypeDeclaration(IdentifierEntity("Json"), emptyList())
            declaration.members.isEmpty() -> TypeDeclaration(IdentifierEntity("Any"), emptyList())
            else -> {
                val ownerEntity = owner?.topmostEntity()
                val ownerUID = ownerEntity?.uid ?: ""

                myAstContext.registerObjectLiteralDeclaration(
                        declaration.copy(members = declaration.members.map { param ->
                            lowerMemberDeclaration(param, owner?.wrap(declaration))
                        }),
                        ownerUID
                )
            }
        }
    }

    fun getContext(): GeneratedInterfacesContext {
        return myAstContext
    }

    override fun lowerVariableDeclaration(declaration: VariableDeclaration, owner: NodeOwner<ModuleDeclaration>?): VariableDeclaration {
        return when (val type = declaration.type) {
            is ObjectLiteralDeclaration -> {
                declaration.copy(type = type.copy(
                        members = type.members.map { member -> lowerMemberDeclaration(member, owner?.wrap(declaration)?.wrap(type)) }
                ))
            }
            else -> declaration.copy(type = lowerParameterValue(type, owner?.wrap(declaration)))
        }
    }

    override fun lowerSourceDeclaration(moduleDeclaration: ModuleDeclaration, owner: NodeOwner<ModuleDeclaration>?): ModuleDeclaration {

        val declarations = moduleDeclaration.declarations.map { declaration ->
            when (declaration) {
                !is TypeAliasDeclaration -> lowerTopLevelDeclaration(declaration, owner)
                else -> declaration
            }
        }.mapNotNull { declaration ->
            when (declaration) {
                is TypeAliasDeclaration -> lowerTopLevelDeclaration(declaration, owner)
                else -> declaration
            }
        }

        return moduleDeclaration.copy(declarations = declarations)
    }
}

private fun ModuleDeclaration.generateInterfaceReferences(generateInterfaceReferences: GenerateInterfaceReferencesDeclarationLowering): ModuleDeclaration {
    return generateInterfaceReferences.getContext().introduceGeneratedEntities(generateInterfaceReferences.lowerSourceDeclaration(this, NodeOwner(this, null)))
}

private fun SourceFileDeclaration.generateInterfaceReferences(generateInterfaceReferences: GenerateInterfaceReferencesDeclarationLowering): SourceFileDeclaration {
    return copy(root = root.generateInterfaceReferences(generateInterfaceReferences))
}

private fun SourceSetDeclaration.generateInterfaceReferences(): SourceSetDeclaration {
    val generateInterfaceReferences = GenerateInterfaceReferencesDeclarationLowering()
    return copy(sources = sources.map { it.generateInterfaceReferences(generateInterfaceReferences) })
}

class GenerateInterfaceReferences() : TsLowering {
    override fun lower(source: SourceSetDeclaration): SourceSetDeclaration {
        return source.generateInterfaceReferences()
    }
}