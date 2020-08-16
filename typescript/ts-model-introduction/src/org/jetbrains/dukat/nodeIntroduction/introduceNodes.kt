package org.jetbrains.dukat.nodeIntroduction

import org.jetbrains.dukat.astCommon.Lowering
import org.jetbrains.dukat.astCommon.MemberEntity
import org.jetbrains.dukat.astCommon.TopLevelEntity
import org.jetbrains.dukat.panic.raiseConcern
import org.jetbrains.dukat.tsmodel.CallSignatureDeclaration
import org.jetbrains.dukat.tsmodel.ClassDeclaration
import org.jetbrains.dukat.tsmodel.ConstructorDeclaration
import org.jetbrains.dukat.tsmodel.EnumDeclaration
import org.jetbrains.dukat.tsmodel.FunctionDeclaration
import org.jetbrains.dukat.tsmodel.GeneratedInterfaceDeclaration
import org.jetbrains.dukat.tsmodel.InterfaceDeclaration
import org.jetbrains.dukat.tsmodel.MemberDeclaration
import org.jetbrains.dukat.tsmodel.MethodDeclaration
import org.jetbrains.dukat.tsmodel.MethodSignatureDeclaration
import org.jetbrains.dukat.tsmodel.ModifierDeclaration
import org.jetbrains.dukat.tsmodel.ModuleDeclaration
import org.jetbrains.dukat.tsmodel.ModuleDeclarationKind
import org.jetbrains.dukat.tsmodel.PropertyDeclaration
import org.jetbrains.dukat.tsmodel.SourceFileDeclaration
import org.jetbrains.dukat.tsmodel.SourceSetDeclaration
import org.jetbrains.dukat.tsmodel.TopLevelDeclaration
import org.jetbrains.dukat.tsmodel.TypeAliasDeclaration
import org.jetbrains.dukat.tsmodel.VariableDeclaration
import org.jetbrains.dukat.tsmodel.types.IndexSignatureDeclaration
import org.jetbrains.dukat.tsmodel.types.makeNullable

fun convertMemberDeclaration(declaration: MemberEntity, inDeclaredDeclaration: Boolean): MemberDeclaration? {
    return when (declaration) {
        is MethodDeclaration -> declaration
        is MethodSignatureDeclaration -> declaration
        is CallSignatureDeclaration -> declaration
        is PropertyDeclaration -> convertPropertyDeclaration(declaration, inDeclaredDeclaration)
        is IndexSignatureDeclaration -> declaration
        is ConstructorDeclaration -> declaration
        else -> raiseConcern("unknown member declaration ${declaration::class.java}") { null }
    }
}

fun convertPropertyDeclaration(declaration: PropertyDeclaration, inDeclaredDeclaration: Boolean): PropertyDeclaration {
    val parameterValueDeclaration = if (declaration.optional) declaration.type.makeNullable() else declaration.type
    return declaration.copy(
            type = parameterValueDeclaration.convertToNode(),
            explicitlyDeclaredType = inDeclaredDeclaration || declaration.explicitlyDeclaredType
    )
}

private class LowerDeclarationsToNodes(private val rootIsDeclaration: Boolean) {

    private fun GeneratedInterfaceDeclaration.convert(): InterfaceDeclaration {
        return InterfaceDeclaration(
                name = name,
                members = members,
                typeParameters = typeParameters,
                parentEntities = parentEntities,
                uid = uid,
                modifiers = setOf(ModifierDeclaration.DECLARE_KEYWORD),
                definitionsInfo = emptyList()
        )
    }

    fun lowerVariableDeclaration(declaration: VariableDeclaration): VariableDeclaration {
        val type = declaration.type
        return declaration.copy(
                type = type.convertToNode(),
                explicitlyDeclaredType = rootIsDeclaration || declaration.explicitlyDeclaredType
        )
    }

    private fun lowerTopLevelDeclaration(declaration: TopLevelEntity): TopLevelDeclaration? {
        return when (declaration) {
            is VariableDeclaration -> lowerVariableDeclaration(declaration)
            is FunctionDeclaration -> declaration
            is ClassDeclaration -> declaration
            is InterfaceDeclaration -> declaration
            is GeneratedInterfaceDeclaration -> declaration.convert()
            is ModuleDeclaration -> lowerPackageDeclaration(declaration)
            is EnumDeclaration -> declaration
            is TypeAliasDeclaration -> declaration
            else -> null
        }
    }


    @Suppress("UNCHECKED_CAST")
    fun lowerPackageDeclaration(documentRoot: ModuleDeclaration): ModuleDeclaration {
        val declarations = documentRoot.declarations.mapNotNull { declaration ->
                lowerTopLevelDeclaration(declaration)
        }

        return documentRoot.copy(
                declarations = declarations
        )
    }
}


class IntroduceNodes : Lowering<SourceSetDeclaration, SourceSetDeclaration> {
    private fun SourceFileDeclaration.introduceNodes(): SourceFileDeclaration {
        return SourceFileDeclaration(
                fileName,
                LowerDeclarationsToNodes(root.kind == ModuleDeclarationKind.DECLARATION_FILE).lowerPackageDeclaration(root)
        )
    }

    override fun lower(source: SourceSetDeclaration): SourceSetDeclaration {
        return SourceSetDeclaration(sourceName = source.sourceName, sources = source.sources.map { sourceFile ->
            sourceFile.introduceNodes()
        })
    }
}