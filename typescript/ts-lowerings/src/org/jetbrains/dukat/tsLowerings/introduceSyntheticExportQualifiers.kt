package org.jetbrains.dukat.tsLowerings

import MergeableDeclaration
import TopLevelDeclarationLowering
import org.jetbrains.dukat.ownerContext.NodeOwner
import org.jetbrains.dukat.tsmodel.ClassDeclaration
import org.jetbrains.dukat.tsmodel.ExportAssignmentDeclaration
import org.jetbrains.dukat.tsmodel.FunctionDeclaration
import org.jetbrains.dukat.tsmodel.InterfaceDeclaration
import org.jetbrains.dukat.tsmodel.ModifierDeclaration
import org.jetbrains.dukat.tsmodel.ModuleDeclaration
import org.jetbrains.dukat.tsmodel.SourceSetDeclaration
import org.jetbrains.dukat.tsmodel.VariableDeclaration

class SyntheticExportModifiersLowering(private val exportAssignments: Map<String, Boolean>) : TopLevelDeclarationLowering {

    private fun MergeableDeclaration.resolveSyntheticExport(): List<ModifierDeclaration> {
        return exportAssignments[uid]?.let { exportAssignment ->
            if (exportAssignment) {
                listOf(ModifierDeclaration.SYNTH_EXPORT_ASSIGNMENT)
            } else {
                listOf(ModifierDeclaration.EXPORT_KEYWORD, ModifierDeclaration.DEFAULT_KEYWORD)
            }
        } ?: emptyList()
    }

    override fun lowerClassDeclaration(declaration: ClassDeclaration, owner: NodeOwner<ModuleDeclaration>?): ClassDeclaration {
        return declaration.copy(modifiers = declaration.modifiers + declaration.resolveSyntheticExport())
    }

    override fun lowerInterfaceDeclaration(declaration: InterfaceDeclaration, owner: NodeOwner<ModuleDeclaration>?): InterfaceDeclaration {
        return declaration.copy(modifiers = declaration.modifiers + declaration.resolveSyntheticExport())
    }

    override fun lowerFunctionDeclaration(declaration: FunctionDeclaration, owner: NodeOwner<ModuleDeclaration>?): FunctionDeclaration {
        return declaration.copy(modifiers = declaration.modifiers + declaration.resolveSyntheticExport())
    }

    override fun lowerVariableDeclaration(declaration: VariableDeclaration, owner: NodeOwner<ModuleDeclaration>?): VariableDeclaration {
        return declaration.copy(modifiers = declaration.modifiers + declaration.resolveSyntheticExport())
    }

    override fun lowerModuleModel(moduleDeclaration: ModuleDeclaration, owner: NodeOwner<ModuleDeclaration>?): ModuleDeclaration? {
        return super.lowerModuleModel(moduleDeclaration.copy(modifiers = moduleDeclaration.modifiers + moduleDeclaration.resolveSyntheticExport()), owner)
    }
}

fun ModuleDeclaration.collectExportAssignments(): List<ExportAssignmentDeclaration> {
    return listOfNotNull(export) + declarations.filterIsInstance(ModuleDeclaration::class.java).flatMap { it.collectExportAssignments() }
}

class IntroduceSyntheticExportModifiers : TsLowering {
    override fun lower(source: SourceSetDeclaration): SourceSetDeclaration {
        return source.copy(sources = source.sources.map {
            val exportAssignments = it.root.collectExportAssignments().flatMap { it.uids.map { uid -> Pair(uid, it.isExportEquals) } }.toMap()
            it.copy(root = SyntheticExportModifiersLowering(exportAssignments).lowerSourceDeclaration(it.root))
        })
    }
}