package org.jetbrains.dukat.idlLowerings

import org.jetbrains.dukat.idlDeclarations.IDLDictionaryDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLEnumDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLFileDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLInterfaceDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLSourceSetDeclaration
import org.jetbrains.dukat.idlDeclarations.InterfaceKind

private class PartialContext : IDLLowering {
    private val interfaces: MutableMap<String, MutableList<IDLInterfaceDeclaration>> = mutableMapOf()
    private val dictionaries: MutableMap<String, MutableList<IDLDictionaryDeclaration>> = mutableMapOf()
    private val enums: MutableMap<String, MutableList<IDLEnumDeclaration>> = mutableMapOf()

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

    override fun lowerEnumDeclaration(declaration: IDLEnumDeclaration): IDLEnumDeclaration {
        enums.getOrPut(declaration.name) { mutableListOf() }.add(declaration)
        return declaration
    }

    fun getInterfaces(name: String): List<IDLInterfaceDeclaration> {
        return interfaces[name].orEmpty()
    }

    fun getDictionaries(name: String): List<IDLDictionaryDeclaration> {
        return dictionaries[name].orEmpty()
    }

    fun getEnums(name: String): List<IDLEnumDeclaration> {
        return enums[name].orEmpty()
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
        var partials = context.getInterfaces(declaration.name)
        partials = partials.filterNot { it.partial } + partials.filter { it.partial }
        context.addResolvedPartial(declaration.name)
        return IDLInterfaceDeclaration(
                name = declaration.name,
                attributes = partials.flatMap { it.attributes }.distinctBy { it.name },
                operations = partials.flatMap { it.operations }.distinct(),
                //at this stage there are no constructors
                primaryConstructor = null,
                constructors = listOf(),
                parents = partials.flatMap { it.parents }.distinct(),
                extendedAttributes = partials.flatMap { it.extendedAttributes }.distinct(),
                getters = partials.flatMap { it.getters }.distinct(),
                setters = partials.flatMap { it.setters }.distinct(),
                callback = partials.any { it.callback },
                generated = partials.any { it.generated },
                partial = false,
                kind = InterfaceKind.INTERFACE
        )
    }

    override fun lowerDictionaryDeclaration(declaration: IDLDictionaryDeclaration): IDLDictionaryDeclaration {
        if (declaration.partial || context.isAlreadyResolved(declaration.name)) {
            return declaration.copy(
                    partial = true
            )
        }
        var partials = context.getDictionaries(declaration.name)
        partials = partials.filterNot { it.partial } + partials.filter { it.partial }
        context.addResolvedPartial(declaration.name)
        return IDLDictionaryDeclaration(
                name = declaration.name,
                members = partials.flatMap { it.members }.distinctBy { it.name },
                parents = partials.flatMap { it.parents }.distinct(),
                partial = false
        )
    }

    override fun lowerEnumDeclaration(declaration: IDLEnumDeclaration): IDLEnumDeclaration {
        if (declaration.partial || context.isAlreadyResolved(declaration.name)) {
            return declaration.copy(
                    partial = true
            )
        }
        val partials = context.getEnums(declaration.name)
        context.addResolvedPartial(declaration.name)
        return IDLEnumDeclaration(
                name = declaration.name,
                members = partials.flatMap { it.members }.distinct()
        )
    }

    override fun lowerFileDeclaration(fileDeclaration: IDLFileDeclaration): IDLFileDeclaration {
        val newFileDeclaration = super.lowerFileDeclaration(fileDeclaration)
        return newFileDeclaration.copy(
                declarations = newFileDeclaration.declarations.filter {
                    when (it) {
                        is IDLInterfaceDeclaration -> !it.partial
                        is IDLDictionaryDeclaration -> !it.partial
                        is IDLEnumDeclaration -> !it.partial
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