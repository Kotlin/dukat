package org.jetbrains.dukat.idlParser.visitors

import org.antlr.webidl.WebIDLBaseVisitor
import org.antlr.webidl.WebIDLParser
import org.jetbrains.dukat.idlDeclarations.IDLConstantDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLTypeDeclaration
import org.jetbrains.dukat.idlParser.getName

internal class ConstantVisitor : WebIDLBaseVisitor<IDLConstantDeclaration>() {
    private var name = ""
    private var type = IDLTypeDeclaration("")

    override fun defaultResult() = IDLConstantDeclaration(name, type)

    override fun visitConst_(ctx: WebIDLParser.Const_Context): IDLConstantDeclaration {
        name = ctx.getName()
        visitChildren(ctx)
        return defaultResult()
    }

    override fun visitConstType(ctx: WebIDLParser.ConstTypeContext): IDLConstantDeclaration {
        type = TypeVisitor().visit(ctx)
        return defaultResult()
    }
}