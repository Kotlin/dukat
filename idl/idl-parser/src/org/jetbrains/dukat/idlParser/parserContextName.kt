package org.jetbrains.dukat.idlParser

import org.antlr.v4.runtime.ParserRuleContext
import org.antlr.v4.runtime.tree.ParseTree
import org.antlr.v4.runtime.tree.TerminalNode
import org.antlr.webidl.WebIDLLexer

internal fun ParserRuleContext.filterIdentifiers(): List<ParseTree> = children?.filter {
    it is TerminalNode && it.symbol.type == WebIDLLexer.IDENTIFIER_WEBIDL
} ?: emptyList()

internal fun ParserRuleContext.getName(): String = filterIdentifiers().first().text

internal fun ParserRuleContext.getNameOrNull(): String? = filterIdentifiers().firstOrNull()?.text

internal fun ParserRuleContext.getFirstValueOrNull(): String? =
        children?.filterIsInstance(TerminalNode::class.java)?.firstOrNull()?.text