package org.jetbrains.dukat.idlParser.visitors

import org.antlr.webidl.WebIDLBaseVisitor
import org.antlr.webidl.WebIDLParser
import org.jetbrains.dukat.idlDeclarations.IDLArgumentDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLExtendedAttributeDeclaration
import org.jetbrains.dukat.idlParser.getNameOrNull

class ExtendedAttributeVisitor : WebIDLBaseVisitor<IDLExtendedAttributeDeclaration>() {

    private var attributeName: String? = null
    private var functionName: String? = null
    private var functionArguments = mutableListOf<IDLArgumentDeclaration>()

    override fun defaultResult() = IDLExtendedAttributeDeclaration(attributeName, functionName, functionArguments)

    override fun visitExtendedAttribute(ctx: WebIDLParser.ExtendedAttributeContext): IDLExtendedAttributeDeclaration {
        attributeName = ctx.getNameOrNull()
        return defaultResult()
    }
}
