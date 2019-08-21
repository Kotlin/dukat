package org.jetbrains.dukat.idlDeclarations

data class IDLInterfaceDeclaration(
        val name: String,
        val attributes: List<IDLAttributeDeclaration>,
        val operations: List<IDLOperationDeclaration>,
        val primaryConstructor: IDLConstructorDeclaration?,
        val constructors: List<IDLConstructorDeclaration>,
        val parents: List<IDLSingleTypeDeclaration>,
        val extendedAttributes: List<IDLExtendedAttributeDeclaration>,
        val getters: List<IDLGetterDeclaration>,
        val setters: List<IDLSetterDeclaration>,
        val kind: InterfaceKind,
        val callback: Boolean,
        val generated: Boolean,
        val partial: Boolean
) : IDLClassLikeDeclaration

enum class InterfaceKind {
    INTERFACE, ABSTRACT_CLASS, OPEN_CLASS
}