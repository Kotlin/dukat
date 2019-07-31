package org.jetbrains.dukat.idlLowerings

import org.jetbrains.dukat.idlDeclarations.IDLFileDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLImplementsStatementDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLInterfaceDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLSingleTypeDeclaration
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

    override fun lowerImplementStatementDeclaration(declaration: IDLImplementsStatementDeclaration): IDLTopLevelDeclaration {
        registerImplementsStatement(declaration)
        return declaration
    }
}

private class ImplementsStatementResolver(val context: ImplementsStatementContext) : IDLLowering {
    override fun lowerInterfaceDeclaration(declaration: IDLInterfaceDeclaration): IDLInterfaceDeclaration {
        return declaration.copy(parents = declaration.parents + context.getMissingInheritances(declaration))
    }
}

fun IDLFileDeclaration.resolveImplementsStatements(): IDLFileDeclaration {
    val context = ImplementsStatementContext()
    return ImplementsStatementResolver(context).lowerFileDeclaration(context.lowerFileDeclaration(this))
}