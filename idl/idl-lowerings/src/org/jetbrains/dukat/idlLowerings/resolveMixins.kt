package org.jetbrains.dukat.idlLowerings

import org.jetbrains.dukat.idlDeclarations.IDLImplementsStatementDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLIncludesStatementDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLInterfaceDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLSourceSetDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLTopLevelDeclaration

private class MixinResolver(val context: MixinContext) : IDLLowering {
    override fun lowerInterfaceDeclaration(declaration: IDLInterfaceDeclaration): IDLInterfaceDeclaration {
        val includedMixins = context.getIncludedMixins(declaration)
        return declaration.copy(
                attributes = (declaration.attributes + includedMixins.flatMap { it.attributes }).distinct(),
                operations = (declaration.operations + includedMixins.flatMap { it.operations }).distinct(),
                constants = (declaration.constants + includedMixins.flatMap { it.constants }).distinct()
        )
    }
}

private class MixinContext : IDLLowering {
    private val mixins : MutableMap<String, IDLInterfaceDeclaration> = mutableMapOf()
    private val includeStatements : MutableMap<String, MutableList<String>> = mutableMapOf()

    override fun lowerInterfaceDeclaration(declaration: IDLInterfaceDeclaration): IDLInterfaceDeclaration {
        if (declaration.mixin) {
            mixins[declaration.name] = declaration
        }
        return declaration
    }

    override fun lowerIncludesStatementDeclaration(declaration: IDLIncludesStatementDeclaration): IDLIncludesStatementDeclaration {
        if (includeStatements[declaration.child.name] == null) {
            includeStatements[declaration.child.name] = mutableListOf()
        }
        includeStatements[declaration.child.name]!!.add(declaration.parent.name)
        return declaration
    }

    fun getIncludedMixins(declaration: IDLInterfaceDeclaration) : List<IDLInterfaceDeclaration> {
        return includeStatements[declaration.name].orEmpty().mapNotNull { mixins[it] }
    }
}

fun IDLSourceSetDeclaration.resolveMixins() : IDLSourceSetDeclaration {
    val context = MixinContext()
    return MixinResolver(context).lowerSourceSetDeclaration(
            context.lowerSourceSetDeclaration(this)
    )
}