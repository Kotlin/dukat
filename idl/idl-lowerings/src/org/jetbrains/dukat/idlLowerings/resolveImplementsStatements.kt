package org.jetbrains.dukat.idlLowerings

import org.jetbrains.dukat.idlDeclarations.*

private class ImplementsStatementContext : IDLLowering {
    private val missingInheritances: MutableMap<String, MutableList<IDLTypeDeclaration>> = mutableMapOf()

    private fun registerImplementsStatement(declaration: IDLImplementsStatementDeclaration) {
        missingInheritances.putIfAbsent(declaration.child.name, mutableListOf())
        missingInheritances[declaration.child.name]!!.add(IDLTypeDeclaration(declaration.parent.name, null, false))
    }

    fun getMissingInheritances(declaration: IDLInterfaceDeclaration): List<IDLTypeDeclaration> {
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

fun IDLSourceSetDeclaration.resolveImplementsStatemets(): IDLSourceSetDeclaration {
    val context = ImplementsStatementContext()
    return ImplementsStatementResolver(context).lowerSourceSetDeclaration(
            context.lowerSourceSetDeclaration(this)
    )
}