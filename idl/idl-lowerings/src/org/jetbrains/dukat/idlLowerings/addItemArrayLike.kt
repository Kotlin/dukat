package org.jetbrains.dukat.idlLowerings

import org.jetbrains.dukat.idlDeclarations.IDLArgumentDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLAttributeDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLFileDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLInterfaceDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLOperationDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLSimpleExtendedAttributeDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLSingleTypeDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLSourceSetDeclaration
import org.jetbrains.dukat.idlDeclarations.toNotNullable

private class ItemArrayLikeLowering : IDLLowering {

    private var alreadyAddedClassesFromStdlib: Boolean = false

    override fun lowerInterfaceDeclaration(declaration: IDLInterfaceDeclaration): IDLInterfaceDeclaration {
        val lengthAttribute = declaration.attributes.find {
            it.name == "length" && it.type == IDLSingleTypeDeclaration("unsignedlong", null, false)
        }
        val itemGetter = declaration.getters.find {
            it.name == "item" &&
                    it.key.type == IDLSingleTypeDeclaration("unsignedlong", null, false) &&
                    it.valueType.nullable
        }
        if (lengthAttribute != null && itemGetter != null) {
            return declaration.copy(parents = declaration.parents + IDLSingleTypeDeclaration(
                    name = "ItemArrayLike",
                    typeParameter = itemGetter.valueType.toNotNullable(),
                    nullable = false
            ))
        }
        return declaration
    }

    override fun lowerFileDeclaration(fileDeclaration: IDLFileDeclaration): IDLFileDeclaration {
        if (alreadyAddedClassesFromStdlib) {
            return super.lowerFileDeclaration(fileDeclaration)
        }
        alreadyAddedClassesFromStdlib = true
        val itemArrayLikeDeclaration = IDLInterfaceDeclaration(
                name = "ItemArrayLike",
                attributes = listOf(IDLAttributeDeclaration(
                        name = "length",
                        type = IDLSingleTypeDeclaration("unsignedlong", null, false),
                        static = false,
                        readOnly = true
                )),
                constants = listOf(),
                operations = listOf(IDLOperationDeclaration(
                        name = "item",
                        returnType = IDLSingleTypeDeclaration("any", null, false),
                        arguments = listOf(IDLArgumentDeclaration(
                                name = "index",
                                type = IDLSingleTypeDeclaration("unsignedlong", null, false)
                        )),
                        static = false
                )),
                primaryConstructor = null,
                constructors = listOf(),
                parents = listOf(),
                extendedAttributes = listOf(
                        IDLSimpleExtendedAttributeDeclaration("NoInterfaceObject")
                ),
                getters = listOf(),
                setters = listOf(),
                callback = false,
                generated = true,
                partial = false,
                fromStdlib = true
        )
        val newFileDeclaration = super.lowerFileDeclaration(fileDeclaration)
        return newFileDeclaration.copy(
                declarations = newFileDeclaration.declarations + itemArrayLikeDeclaration
        )
    }
}

fun IDLSourceSetDeclaration.addItemArrayLike(): IDLSourceSetDeclaration {
    return ItemArrayLikeLowering().lowerSourceSetDeclaration(this)
}