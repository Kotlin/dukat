package org.jetbrains.dukat.idlDeclarations

interface IDLExtendedAttributeDeclaration: IDLDeclaration

data class IDLSimpleExtendedAttributeDeclaration(
        val attributeName: String
): IDLExtendedAttributeDeclaration

data class IDLFunctionExtendedAttributeDeclaration(
        val functionName: String,
        val arguments: List<IDLArgumentDeclaration>
): IDLExtendedAttributeDeclaration

data class IDLNamedFunctionExtendedAttributeDeclaration(
        val name: String,
        val functionName: String,
        val arguments: List<IDLArgumentDeclaration>
): IDLExtendedAttributeDeclaration

data class IDLAssignmentExtendedAttributeDeclaration(
        val leftSide: String,
        val rightSide: String
): IDLExtendedAttributeDeclaration

data class IDLListAssignmentExtendedAttributeDeclaration(
        val leftSide: String,
        val rightSide: List<String>
): IDLExtendedAttributeDeclaration