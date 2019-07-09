package org.jetbrains.dukat.idlParser

import org.antlr.v4.runtime.ParserRuleContext
import org.antlr.v4.runtime.tree.ParseTree
import org.antlr.v4.runtime.tree.TerminalNode
import org.antlr.webidl.WebIDLLexer

internal fun filterIdentifiers(list: List<ParseTree>?): List<ParseTree> = list?.filter {
    it is TerminalNode && it.symbol.type == WebIDLLexer.IDENTIFIER_WEBIDL
} ?: emptyList()

internal fun ParserRuleContext.getName(): String = filterIdentifiers(children).first().text

internal fun ParserRuleContext.getNameOrNull(): String? = filterIdentifiers(children).firstOrNull()?.text
