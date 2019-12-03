package org.jetbrains.dukat.idlLowerings

import org.jetbrains.dukat.idlDeclarations.IDLFileDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLInterfaceDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLSimpleExtendedAttributeDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLSourceSetDeclaration
import org.jetbrains.dukat.idlDeclarations.InterfaceKind

private class AbstractOrOpenContext : IDLLowering {
    private val interfaces: MutableMap<String, IDLInterfaceDeclaration> = mutableMapOf()

    override fun lowerInterfaceDeclaration(declaration: IDLInterfaceDeclaration, owner: IDLFileDeclaration): IDLInterfaceDeclaration {
        interfaces[declaration.name] = declaration
        return declaration
    }

    fun resolveInterface(interfaceName: String): IDLInterfaceDeclaration? {
        return interfaces[interfaceName]
    }

}

private class AbstractOrOpenMarker(val context: AbstractOrOpenContext) : IDLLowering {

    private fun getAllParents(declaration: IDLInterfaceDeclaration): List<IDLInterfaceDeclaration> {
        val firstLevelParents = declaration.parents.mapNotNull { context.resolveInterface(it.name) }
        return firstLevelParents + firstLevelParents.flatMap { getAllParents(it) }
    }

    private fun IDLInterfaceDeclaration.shouldBeConvertedToInterface(): Boolean {
        return callback || extendedAttributes.contains(
                IDLSimpleExtendedAttributeDeclaration("NoInterfaceObject")
        )
    }

    private fun IDLInterfaceDeclaration.shouldBeConvertedToOpenClass(): Boolean {
        return (getAllParents(this) + this).any {
            it.constructors.isNotEmpty() || it.primaryConstructor != null
        }
    }

    private fun IDLInterfaceDeclaration.shouldBeConvertedToAbstractClass(): Boolean {
        return !shouldBeConvertedToInterface() && !shouldBeConvertedToOpenClass()
    }

    override fun lowerInterfaceDeclaration(declaration: IDLInterfaceDeclaration, owner: IDLFileDeclaration): IDLInterfaceDeclaration {
        return declaration.copy(
                kind = when {
                    declaration.shouldBeConvertedToInterface() -> InterfaceKind.INTERFACE
                    declaration.shouldBeConvertedToOpenClass() -> InterfaceKind.OPEN_CLASS
                    else -> InterfaceKind.ABSTRACT_CLASS
                },
                attributes = declaration.attributes.map {
                    it.copy(open = when {
                        it.static -> false
                        declaration.shouldBeConvertedToAbstractClass() -> true
                        declaration.shouldBeConvertedToOpenClass() && it.readOnly -> true
                        else -> false
                    })
                }
        )

    }
}

fun IDLSourceSetDeclaration.markAbstractOrOpen(): IDLSourceSetDeclaration {
    val context = AbstractOrOpenContext()
    return AbstractOrOpenMarker(context).lowerSourceSetDeclaration(
            context.lowerSourceSetDeclaration(this)
    )
}