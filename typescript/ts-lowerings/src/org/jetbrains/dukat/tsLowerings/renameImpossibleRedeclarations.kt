package org.jetbrains.dukat.tsLowerings

import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astCommon.QualifierEntity
import org.jetbrains.dukat.ownerContext.NodeOwner
import org.jetbrains.dukat.tsmodel.InterfaceDeclaration
import org.jetbrains.dukat.tsmodel.ModuleDeclaration
import org.jetbrains.dukat.tsmodel.ParameterOwnerDeclaration
import org.jetbrains.dukat.tsmodel.SourceFileDeclaration
import org.jetbrains.dukat.tsmodel.SourceSetDeclaration
import org.jetbrains.dukat.tsmodel.importClause.NamedImportsDeclaration
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration

private fun NameEntity.addPrefix(prefix: String): NameEntity {
    return when(this) {
        is IdentifierEntity -> IdentifierEntity(prefix + value)
        is QualifierEntity -> copy(right = right.addPrefix(prefix) as IdentifierEntity)
    }
}

private class RenameImpossibleDeclarationsTypeLowering(private val namedImports: Set<IdentifierEntity>) : DeclarationTypeLowering {

    override fun lowerInterfaceDeclaration(declaration: InterfaceDeclaration, owner: NodeOwner<ModuleDeclaration>?): InterfaceDeclaration {
        val declarationResolved = if (namedImports.contains(declaration.name)) { declaration.copy(name = declaration.name.addPrefix("Local")) } else { declaration }
        return super.lowerInterfaceDeclaration(declarationResolved, owner)
    }
}

private fun ModuleDeclaration.renameImpossibleDeclarations(): ModuleDeclaration {
    val namedImports = imports
            .map { it.clause }
            .filterIsInstance(NamedImportsDeclaration::class.java)
            .flatMap { it.importSpecifiers }
            .mapNotNull { it.propertyName?.let { IdentifierEntity(it) } }.toSet()
    return RenameImpossibleDeclarationsTypeLowering(namedImports).lowerDocumentRoot(this)
}

private fun SourceFileDeclaration.renameImpossibleDeclarations(): SourceFileDeclaration {
    return copy(root = root.renameImpossibleDeclarations())
}

private fun SourceSetDeclaration.renameImpossibleDeclarations(): SourceSetDeclaration {
    return copy(sources = sources.map(SourceFileDeclaration::renameImpossibleDeclarations))
}

class RenameImpossibleDeclarations() : TsLowering {
    override fun lower(source: SourceSetDeclaration): SourceSetDeclaration {
        return source.renameImpossibleDeclarations()
    }
}