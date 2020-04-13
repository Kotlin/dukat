package org.jetbrains.dukat.tsLowerings

import TopLevelDeclarationLowering
import org.jetbrains.dukat.ownerContext.NodeOwner
import org.jetbrains.dukat.tsmodel.ModuleDeclaration
import org.jetbrains.dukat.tsmodel.SourceSetDeclaration


private class MergeModulesLowering(private val topLevelDeclarationResolver: TopLevelDeclarationResolver): TopLevelDeclarationLowering {

    private fun merge(a: ModuleDeclaration, b: ModuleDeclaration, owner: NodeOwner<ModuleDeclaration>?): ModuleDeclaration {
        return a.copy(declarations = (a.declarations + b.declarations).mapNotNull { lowerTopLevelDeclaration(it, owner) })
    }

    override fun lowerModuleModel(moduleDeclaration: ModuleDeclaration, owner: NodeOwner<ModuleDeclaration>?): ModuleDeclaration? {
        val definitions = moduleDeclaration.definitionsInfo
                .mapNotNull { definition -> topLevelDeclarationResolver.resolve(definition.uid) }
                .filterIsInstance(ModuleDeclaration::class.java)

        val onlyModules = definitions.isNotEmpty()
        return if (onlyModules) {
            if (moduleDeclaration.uid == definitions.firstOrNull()?.uid) {
                definitions.reduce { acc, definitionInfoDeclaration -> merge(acc, definitionInfoDeclaration, owner) }
            } else {
                null
            }
        } else {
            moduleDeclaration
        }
    }
}

private fun SourceSetDeclaration.mergeModules(topLevelDeclarationResolver: TopLevelDeclarationResolver): SourceSetDeclaration {
    return copy(sources = sources.map { it.copy(root = MergeModulesLowering(topLevelDeclarationResolver).lowerSourceDeclaration(it.root)) })
}

class MergeModules() : TsLowering {
    override fun lower(source: SourceSetDeclaration): SourceSetDeclaration {
        val topLevelDeclarationResolver = TopLevelDeclarationResolver(source)
        return source.mergeModules(topLevelDeclarationResolver)
    }
}