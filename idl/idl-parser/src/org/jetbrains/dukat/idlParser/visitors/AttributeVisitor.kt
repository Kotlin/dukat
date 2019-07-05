package org.jetbrains.dukat.idlParser.visitors

import org.antlr.v4.runtime.tree.TerminalNode
import org.antlr.webidl.WebIDLBaseVisitor
import org.antlr.webidl.WebIDLParser
import org.jetbrains.dukat.idlDeclarations.IDLAttributeDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLTypeDeclaration
import org.jetbrains.dukat.idlParser.getNameOrNull

internal class AttributeVisitor : WebIDLBaseVisitor<IDLAttributeDeclaration>() {
    private var name: String = ""
    private var type: IDLTypeDeclaration = IDLTypeDeclaration("")

    override fun defaultResult(): IDLAttributeDeclaration = IDLAttributeDeclaration(name, type)

    override fun visitType(ctx: WebIDLParser.TypeContext): IDLAttributeDeclaration {
        type = TypeVisitor().visit(ctx)
        return defaultResult()
    }

    override fun visitAttributeRest(ctx: WebIDLParser.AttributeRestContext): IDLAttributeDeclaration {
        name = getNameOrNull(ctx) ?: ctx.children.filter { it is TerminalNode }.filter { it.text != ";" }.last().text
        visitChildren(ctx)
        return defaultResult()
    }
}
