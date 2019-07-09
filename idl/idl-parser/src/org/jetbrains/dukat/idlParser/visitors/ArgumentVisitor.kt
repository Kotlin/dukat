package org.jetbrains.dukat.idlParser.visitors

import org.antlr.webidl.WebIDLBaseVisitor
import org.antlr.webidl.WebIDLParser
import org.jetbrains.dukat.idlDeclarations.IDLArgumentDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLTypeDeclaration
import org.jetbrains.dukat.idlParser.getNameOrNull

internal class ArgumentVisitor: WebIDLBaseVisitor<IDLArgumentDeclaration>() {
    private var name: String = ""
    private var type: IDLTypeDeclaration = IDLTypeDeclaration("")

    override fun defaultResult(): IDLArgumentDeclaration {
        return IDLArgumentDeclaration(name, type)
    }

    override fun visitType(ctx: WebIDLParser.TypeContext): IDLArgumentDeclaration {
        type = TypeVisitor().visit(ctx)
        return defaultResult()
    }

    override fun visitArgumentName(ctx: WebIDLParser.ArgumentNameContext): IDLArgumentDeclaration {
        name = ctx.text
        return defaultResult()
    }
}
