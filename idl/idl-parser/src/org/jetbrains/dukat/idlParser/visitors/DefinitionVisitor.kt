package org.jetbrains.dukat.idlParser.visitors

import org.antlr.webidl.WebIDLBaseVisitor
import org.antlr.webidl.WebIDLParser
import org.jetbrains.dukat.idlDeclarations.IDLAttributeDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLInterfaceDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLTopLevelDeclaration
import org.jetbrains.dukat.idlParser.getName

internal class DefinitionVisitor : WebIDLBaseVisitor<IDLTopLevelDeclaration>() {
    private val attributes: MutableList<IDLAttributeDeclaration> = mutableListOf()

    override fun visitAttributeRest(ctx: WebIDLParser.AttributeRestContext): IDLTopLevelDeclaration {
        attributes.add(with (AttributeVisitor()) {
            visit(ctx)
            visitAttributeRest(ctx)
        })
        return IDLInterfaceDeclaration("", listOf())
    }

    override fun visitInterface_(ctx: WebIDLParser.Interface_Context): IDLTopLevelDeclaration {
        val name = ctx.getName()
        visitChildren(ctx)
        return IDLInterfaceDeclaration(name, attributes)
    }
}
