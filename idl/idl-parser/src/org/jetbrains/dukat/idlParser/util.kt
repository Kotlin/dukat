package org.jetbrains.dukat.idlParser

import org.antlr.v4.runtime.ParserRuleContext
import org.antlr.v4.runtime.tree.ParseTree
import org.antlr.v4.runtime.tree.TerminalNode
import org.antlr.webidl.WebIDLLexer

internal fun List<ParseTree>?.filterIdentifiers(): List<ParseTree> = this?.filter {
    it is TerminalNode && it.symbol.type == WebIDLLexer.IDENTIFIER_WEBIDL
} ?: emptyList()

internal fun getName(ctx: ParserRuleContext) = ctx.children.filterIdentifiers().first().text

internal fun getNameOrNull(ctx: ParserRuleContext) = ctx.children.filterIdentifiers().firstOrNull()?.text
