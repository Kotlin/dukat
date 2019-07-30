package org.jetbrains.dukat.idlLowerings

import org.jetbrains.dukat.idlDeclarations.IDLDictionaryDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLFileDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLInterfaceDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLSourceSetDeclaration

private class PartialContext : IDLLowering {
    private val interfaces: MutableMap<String, MutableList<IDLInterfaceDeclaration>> = mutableMapOf()
    private val dictionaries: MutableMap<String, MutableList<IDLDictionaryDeclaration>> = mutableMapOf()
    private val alreadyResolvedPartials: MutableList<String> = mutableListOf()

    override fun lowerInterfaceDeclaration(declaration: IDLInterfaceDeclaration): IDLInterfaceDeclaration {
        if (!interfaces.containsKey(declaration.name)) {
            interfaces[declaration.name] = mutableListOf()
        }
        interfaces[declaration.name]!!.add(declaration)
        return declaration
    }

    override fun lowerDictionaryDeclaration(declaration: IDLDictionaryDeclaration): IDLDictionaryDeclaration {
        if (!dictionaries.containsKey(declaration.name)) {
            dictionaries[declaration.name] = mutableListOf()
        }
        dictionaries[declaration.name]!!.add(declaration)
        return declaration
    }

    fun getInterfaces(name: String): List<IDLInterfaceDeclaration> {
        return interfaces[name].orEmpty()
    }

    fun getDictionaries(name: String): List<IDLDictionaryDeclaration> {
        return dictionaries[name].orEmpty()
    }

    fun addResolvedPartial(name: String) {
        alreadyResolvedPartials.add(name)
    }

    fun isAlreadyResolved(name: String): Boolean {
        return name in alreadyResolvedPartials
    }
}

private class PartialResolver(val context: PartialContext) : IDLLowering {

    override fun lowerInterfaceDeclaration(declaration: IDLInterfaceDeclaration): IDLInterfaceDeclaration {
        if (declaration.partial || context.isAlreadyResolved(declaration.name)) {
            return declaration.copy(
                    partial = true
            )
        }
        val partials = context.getInterfaces(declaration.name)
        context.addResolvedPartial(declaration.name)
        return IDLInterfaceDeclaration(
                name = declaration.name,
                attributes = partials.flatMap { it.attributes }.distinct(),
                constants = partials.flatMap { it.constants }.distinct(),
                operations = partials.flatMap { it.operations }.distinct(),
                //at this stage there are no constructors
                primaryConstructor = null,
                constructors = listOf(),
                parents = partials.flatMap { it.parents }.distinct(),
                extendedAttributes = partials.flatMap { it.extendedAttributes }.distinct(),
                partial = false
        )
    }

    override fun lowerDictionaryDeclaration(declaration: IDLDictionaryDeclaration): IDLDictionaryDeclaration {
        if (declaration.partial || context.isAlreadyResolved(declaration.name)) {
            return declaration.copy(
                    partial = true
            )
        }
        val partials = context.getDictionaries(declaration.name)
        context.addResolvedPartial(declaration.name)
        return IDLDictionaryDeclaration(
                name = declaration.name,
                members = partials.flatMap { it.members }.distinct(),
                parents = partials.flatMap { it.parents }.distinct(),
                partial = false
        )
    }

    override fun lowerFileDeclaration(fileDeclaration: IDLFileDeclaration): IDLFileDeclaration {
        val newFileDeclaration = super.lowerFileDeclaration(fileDeclaration)
        return newFileDeclaration.copy(
                declarations = newFileDeclaration.declarations.filter {
                    when (it) {
                        is IDLInterfaceDeclaration -> !it.partial
                        is IDLDictionaryDeclaration -> !it.partial
                        else -> true
                    }
                }
        )
    }

}

fun IDLSourceSetDeclaration.resolvePartials(): IDLSourceSetDeclaration {
    val context = PartialContext()
    return PartialResolver(context).lowerSourceSetDeclaration(
            context.lowerSourceSetDeclaration(this)
    )
}