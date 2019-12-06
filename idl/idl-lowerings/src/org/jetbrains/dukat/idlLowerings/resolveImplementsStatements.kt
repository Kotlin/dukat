package org.jetbrains.dukat.idlLowerings

import org.jetbrains.dukat.idlDeclarations.IDLFileDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLImplementsStatementDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLInterfaceDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLSingleTypeDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLSourceSetDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLTopLevelDeclaration

private class ImplementsStatementContext : IDLLowering {
    private val missingInheritances: MutableMap<String, MutableList<IDLSingleTypeDeclaration>> = mutableMapOf()

    private fun registerImplementsStatement(declaration: IDLImplementsStatementDeclaration) {
        missingInheritances.putIfAbsent(declaration.child.name, mutableListOf())
        missingInheritances[declaration.child.name]!!.add(IDLSingleTypeDeclaration(declaration.parent.name, null, false))
    }

    fun getMissingInheritances(declaration: IDLInterfaceDeclaration): List<IDLSingleTypeDeclaration> {
        return missingInheritances[declaration.name] ?: listOf()
    }

    override fun lowerImplementStatementDeclaration(declaration: IDLImplementsStatementDeclaration, owner: IDLFileDeclaration): IDLImplementsStatementDeclaration {
        registerImplementsStatement(declaration)
        return declaration
    }
}

private class ImplementsStatementResolver(val context: ImplementsStatementContext) : IDLLowering {
    override fun lowerInterfaceDeclaration(declaration: IDLInterfaceDeclaration, owner: IDLFileDeclaration): IDLInterfaceDeclaration {
        return declaration.copy(parents = (declaration.parents + context.getMissingInheritances(declaration)).distinct())
    }
}

fun IDLSourceSetDeclaration.resolveImplementsStatements(): IDLSourceSetDeclaration {
    val context = ImplementsStatementContext()
    return ImplementsStatementResolver(context).lowerSourceSetDeclaration(
            context.lowerSourceSetDeclaration(this)
    )
}