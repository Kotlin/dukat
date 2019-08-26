package org.jetbrains.dukat.idlLowerings

import org.jetbrains.dukat.idlDeclarations.IDLImplementsStatementDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLIncludesStatementDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLInterfaceDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLSourceSetDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLTopLevelDeclaration

private class MixinResolver(val mixinContext: MixinContext, val missingMemberContext: MissingMemberContext) : IDLLowering {
    override fun lowerInterfaceDeclaration(declaration: IDLInterfaceDeclaration): IDLInterfaceDeclaration {
        val includedMixins = mixinContext.getIncludedMixins(declaration)
        val overrideHelper = OverrideHelper(missingMemberContext)
        return declaration.copy(
                attributes = (declaration.attributes + includedMixins.flatMap { it.attributes }.filter { newAttribute ->
                    declaration.attributes.none { oldAttribute ->
                        overrideHelper.isConflicting(newAttribute, oldAttribute) ||
                                overrideHelper.isConflicting(oldAttribute, newAttribute)
                    }
                }).distinct(),
                operations = (declaration.operations + includedMixins.flatMap { it.operations }.filter { newOperation ->
                    declaration.operations.none { oldOperation ->
                        overrideHelper.isConflicting(newOperation, oldOperation) ||
                                overrideHelper.isConflicting(oldOperation, newOperation)
                    }
                }).distinct()
        )
    }
}

private class MixinContext : IDLLowering {
    private val mixins: MutableMap<String, IDLInterfaceDeclaration> = mutableMapOf()
    private val includeStatements: MutableMap<String, MutableList<String>> = mutableMapOf()

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

    fun getIncludedMixins(declaration: IDLInterfaceDeclaration): List<IDLInterfaceDeclaration> {
        return includeStatements[declaration.name].orEmpty().mapNotNull { mixins[it] }
    }
}

fun IDLSourceSetDeclaration.resolveMixins(): IDLSourceSetDeclaration {
    val mixinContext = MixinContext()
    val missingMemberContext = MissingMemberContext()
    return MixinResolver(mixinContext, missingMemberContext).lowerSourceSetDeclaration(
            mixinContext.lowerSourceSetDeclaration(
                    missingMemberContext.lowerSourceSetDeclaration(this)
            )
    )
}