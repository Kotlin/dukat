package org.jetbrains.dukat.idlDeclarations

import org.antlr.v4.runtime.CharStream
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.ParserRuleContext
import org.antlr.v4.runtime.tree.ParseTree
import org.antlr.v4.runtime.tree.TerminalNode
import org.antlr.webidl.WebIDLBaseVisitor
import org.antlr.webidl.WebIDLLexer
import org.antlr.webidl.WebIDLParser
import org.antlr.webidl.WebIDLParser.Interface_Context

class DefinitionVisitor : WebIDLBaseVisitor<IDLDeclaration>() {

    override fun visitInterface_(ctx: Interface_Context): IDLDeclaration {
        val name = getName(ctx)
        visitChildren(ctx)
        return IDLInterfaceDeclaration(name)
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

fun parseIDL(fileName: String): IDLFileDeclaration {
    val reader = CharStreams.fromFileName(fileName, Charsets.UTF_8)

    val lexer = WebIDLLexer(reader)
    val parser = WebIDLParser(CommonTokenStream(lexer))
    val idl = parser.webIDL()

    val declarations = ArrayList<IDLDeclaration>()
    ModuleVisitor(declarations).visit(idl)

    return IDLFileDeclaration(fileName, declarations)
}
