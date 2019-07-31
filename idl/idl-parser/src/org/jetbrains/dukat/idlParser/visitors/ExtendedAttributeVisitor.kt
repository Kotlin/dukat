package org.jetbrains.dukat.idlParser.visitors

import org.antlr.v4.runtime.tree.TerminalNode
import org.antlr.webidl.WebIDLBaseVisitor
import org.antlr.webidl.WebIDLLexer
import org.antlr.webidl.WebIDLParser
import org.jetbrains.dukat.idlDeclarations.IDLArgumentDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLAssignmentExtendedAttributeDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLExtendedAttributeDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLFunctionExtendedAttributeDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLListAssignmentExtendedAttributeDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLNamedFunctionExtendedAttributeDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLSimpleExtendedAttributeDeclaration
import org.jetbrains.dukat.idlParser.getName
import org.jetbrains.dukat.idlParser.getNameOrNull

class ExtendedAttributeVisitor : WebIDLBaseVisitor<IDLExtendedAttributeDeclaration>() {

    private var name: String = ""
    private var rightSideOfAssignment: String? = null
    private var functionArguments = mutableListOf<IDLArgumentDeclaration>()
    private var identifiers = mutableListOf<String>()

    private var hasArgumentList = false
    private var hasIdentifierList = false

    override fun defaultResult(): IDLExtendedAttributeDeclaration {
        if (hasIdentifierList) {
            return IDLListAssignmentExtendedAttributeDeclaration(name, identifiers)
        }
        return if (rightSideOfAssignment != null) {
            if (hasArgumentList) {
                IDLNamedFunctionExtendedAttributeDeclaration(name, rightSideOfAssignment!!, functionArguments)
            } else {
                IDLAssignmentExtendedAttributeDeclaration(name, rightSideOfAssignment!!)
            }
        } else {
            if (hasArgumentList) {
                IDLFunctionExtendedAttributeDeclaration(name, functionArguments)
            } else {
                IDLSimpleExtendedAttributeDeclaration(name)
            }
        }
    }

    override fun visitExtendedAttribute(ctx: WebIDLParser.ExtendedAttributeContext): IDLExtendedAttributeDeclaration {
        visitChildren(ctx)
        if (name.isEmpty()) {
            name = ctx.getName()
        } else {
            rightSideOfAssignment = ctx.getNameOrNull()
        }
        return defaultResult()
    }

    override fun visitExtendedAttributeNamePart(ctx: WebIDLParser.ExtendedAttributeNamePartContext): IDLExtendedAttributeDeclaration {
        name = ctx.getName()
        return defaultResult()
    }

    override fun visitArgumentList(ctx: WebIDLParser.ArgumentListContext): IDLExtendedAttributeDeclaration {
        hasArgumentList = true
        object : WebIDLBaseVisitor<Unit>() {
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
                if (node.symbol.type == WebIDLLexer.IDENTIFIER_WEBIDL) {
                    identifiers.add(node.text)
                }
            }
        }.visit(ctx)
        return defaultResult()
    }

}
