package org.jetbrains.dukat.nodeIntroduction

import org.jetbrains.dukat.astCommon.Lowering
import org.jetbrains.dukat.astCommon.TopLevelEntity
import org.jetbrains.dukat.tsmodel.ClassDeclaration
import org.jetbrains.dukat.tsmodel.EnumDeclaration
import org.jetbrains.dukat.tsmodel.FunctionDeclaration
import org.jetbrains.dukat.tsmodel.GeneratedInterfaceDeclaration
import org.jetbrains.dukat.tsmodel.InterfaceDeclaration
import org.jetbrains.dukat.tsmodel.ModuleDeclaration
import org.jetbrains.dukat.tsmodel.SourceFileDeclaration
import org.jetbrains.dukat.tsmodel.SourceSetDeclaration
import org.jetbrains.dukat.tsmodel.TopLevelDeclaration
import org.jetbrains.dukat.tsmodel.TypeAliasDeclaration
import org.jetbrains.dukat.tsmodel.VariableDeclaration

private class LowerDeclarationsToNodes {
    private fun lowerTopLevelDeclaration(declaration: TopLevelEntity): TopLevelDeclaration? {
        return when (declaration) {
            is VariableDeclaration -> declaration.copy(type = declaration.type.convertToNode())
            is FunctionDeclaration -> declaration
            is ClassDeclaration -> declaration
            is InterfaceDeclaration -> declaration
            is GeneratedInterfaceDeclaration -> declaration
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
                LowerDeclarationsToNodes().lowerPackageDeclaration(root)
        )
    }

    override fun lower(source: SourceSetDeclaration): SourceSetDeclaration {
        return SourceSetDeclaration(sourceName = source.sourceName, sources = source.sources.map { sourceFile ->
            sourceFile.introduceNodes()
        })
    }
}