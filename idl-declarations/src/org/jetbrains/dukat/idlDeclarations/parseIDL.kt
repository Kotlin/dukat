package org.jetbrains.dukat.idlDeclarations

import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.ParserRuleContext
import org.antlr.v4.runtime.tree.ParseTree
import org.antlr.v4.runtime.tree.TerminalNode
import org.antlr.webidl.WebIDLBaseVisitor
import org.antlr.webidl.WebIDLLexer
import org.antlr.webidl.WebIDLParser
import org.antlr.webidl.WebIDLParser.Interface_Context

class TypeVisitor : WebIDLBaseVisitor<IDLTypeDeclaration>() {
    private var type: IDLTypeDeclaration = IDLTypeDeclaration("")

    override fun visitType(ctx: WebIDLParser.TypeContext?): IDLTypeDeclaration {
        super.visitType(ctx)
        return type
    }

    override fun visitPrimitiveType(ctx: WebIDLParser.PrimitiveTypeContext): IDLTypeDeclaration {
        type = IDLTypeDeclaration(ctx.text)
        return type
    }
}

class AttributeVisitor : WebIDLBaseVisitor<IDLAttributeDeclaration>() {
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

class DefinitionVisitor : WebIDLBaseVisitor<IDLDeclaration>() {
    private val attributes: MutableList<IDLAttributeDeclaration> = mutableListOf()

    override fun visitAttributeRest(ctx: WebIDLParser.AttributeRestContext): IDLDeclaration {
        with (AttributeVisitor()) {
            visit(ctx)
            this@DefinitionVisitor.attributes.add(visitAttributeRest(ctx))
        }
        return IDLInterfaceDeclaration("", listOf())
    }

    override fun visitInterface_(ctx: Interface_Context): IDLDeclaration {
        val name = getName(ctx)
        visitChildren(ctx)
        return IDLInterfaceDeclaration(name, attributes)
    }
}

class ModuleVisitor(val declarations: MutableList<IDLDeclaration>) : WebIDLBaseVisitor<Unit>() {

    override fun visitDefinition(ctx: WebIDLParser.DefinitionContext?) {
        declarations.add(DefinitionVisitor().visitDefinition(ctx))
    }

}

private fun List<ParseTree>?.filterIdentifiers(): List<ParseTree> = this?.filter {
    it is TerminalNode && it.symbol.type == WebIDLLexer.IDENTIFIER_WEBIDL
} ?: emptyList()

private fun getName(ctx: ParserRuleContext) = ctx.children.filterIdentifiers().first().text

private fun getNameOrNull(ctx: ParserRuleContext) = ctx.children.filterIdentifiers().firstOrNull()?.text

fun parseIDL(fileName: String): IDLFileDeclaration {
    val reader = CharStreams.fromFileName(fileName, Charsets.UTF_8)

    val lexer = WebIDLLexer(reader)
    val parser = WebIDLParser(CommonTokenStream(lexer))
    val idl = parser.webIDL()

    val declarations = ArrayList<IDLDeclaration>()
    ModuleVisitor(declarations).visit(idl)

    return IDLFileDeclaration(fileName, declarations)
}
