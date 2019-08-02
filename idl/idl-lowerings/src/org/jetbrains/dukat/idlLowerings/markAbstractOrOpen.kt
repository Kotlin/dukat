package org.jetbrains.dukat.idlLowerings

import org.jetbrains.dukat.idlDeclarations.IDLInterfaceDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLSimpleExtendedAttributeDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLSourceSetDeclaration
import org.jetbrains.dukat.idlDeclarations.InterfaceKind

private class AbstractOrOpenContext : IDLLowering {
    private val interfaces: MutableMap<String, MutableList<IDLInterfaceDeclaration>> = mutableMapOf()

    override fun lowerInterfaceDeclaration(declaration: IDLInterfaceDeclaration): IDLInterfaceDeclaration {
        if (!interfaces.containsKey(declaration.name)) {
            interfaces[declaration.name] = mutableListOf()
        }
        interfaces[declaration.name]!!.add(declaration)
        return declaration
    }

}

private class AbstractOrOpenMarker(val context: AbstractOrOpenContext) : IDLLowering {

    override fun lowerInterfaceDeclaration(declaration: IDLInterfaceDeclaration): IDLInterfaceDeclaration {
        return declaration.copy(
                kind = when {
                    declaration.callback || declaration.extendedAttributes.contains(
                            IDLSimpleExtendedAttributeDeclaration("NoInterfaceObject")
                    ) -> {
                        InterfaceKind.INTERFACE
                    }
                    declaration.constructors.isNotEmpty() || declaration.primaryConstructor != null -> {
                        InterfaceKind.OPEN_CLASS
                    }
                    else -> {
                        InterfaceKind.ABSTRACT_CLASS
                    }
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