package org.jetbrains.dukat.idlParser.visitors

import org.antlr.v4.runtime.tree.TerminalNode
import org.antlr.webidl.WebIDLBaseVisitor
import org.antlr.webidl.WebIDLLexer
import org.antlr.webidl.WebIDLParser
import org.jetbrains.dukat.idlDeclarations.*
import org.jetbrains.dukat.idlParser.getName
import org.jetbrains.dukat.idlParser.getNameOrNull

class ExtendedAttributeVisitor : WebIDLBaseVisitor<IDLExtendedAttributeDeclaration>() {

    private var firstString: String = ""
    private var secondString: String? = null
    private var functionArguments = mutableListOf<IDLArgumentDeclaration>()
    private var identifiers = mutableListOf<String>()

    private var hasArgumentList = false
    private var hasIdentifierList = false

    override fun defaultResult(): IDLExtendedAttributeDeclaration {
        if (hasIdentifierList) {
            return IDLListAssignmentExtendedAttributeDeclaration(firstString, identifiers)
        }
        return if (secondString != null) {
            if (hasArgumentList) {
                IDLNamedFunctionExtendedAttributeDeclaration(firstString, secondString!!, functionArguments)
            } else {
                IDLAssignmentExtendedAttributeDeclaration(firstString, secondString!!)
            }
        } else {
            if (hasArgumentList) {
                IDLFunctionExtendedAttributeDeclaration(firstString, functionArguments)
            } else {
                IDLSimpleExtendedAttributeDeclaration(firstString)
            }
        }
    }

    override fun visitExtendedAttribute(ctx: WebIDLParser.ExtendedAttributeContext): IDLExtendedAttributeDeclaration {
        visitChildren(ctx)
        if (firstString == "") {
            firstString = ctx.getName()
        } else {
            secondString = ctx.getNameOrNull()
        }
        return defaultResult()
    }

    override fun visitExtendedAttributeNamePart(ctx: WebIDLParser.ExtendedAttributeNamePartContext): IDLExtendedAttributeDeclaration {
        firstString = ctx.getName()
        return defaultResult()
    }

    override fun visitArgumentList(ctx: WebIDLParser.ArgumentListContext): IDLExtendedAttributeDeclaration {
        hasArgumentList = true
        object: WebIDLBaseVisitor<Unit>() {
            override fun visitArgument(ctx: WebIDLParser.ArgumentContext) {
                functionArguments.add(ArgumentVisitor().visit(ctx))
            }
        }.visit(ctx)
        return defaultResult()
    }

    override fun visitIdentifierList(ctx: WebIDLParser.IdentifierListContext): IDLExtendedAttributeDeclaration {
        hasIdentifierList = true
        object : WebIDLBaseVisitor<Unit>() {
            override fun visitTerminal(node: TerminalNode) {
                if (node.symbol.type == WebIDLLexer.IDENTIFIER_WEBIDL)
                identifiers.add(node.text)
            }
        }.visit(ctx)
        return defaultResult()
    }

}
