package org.jetbrains.dukat.idlParser.visitors

import org.antlr.webidl.WebIDLBaseVisitor
import org.antlr.webidl.WebIDLParser
import org.jetbrains.dukat.idlDeclarations.IDLTypeDeclaration

internal class TypeVisitor : WebIDLBaseVisitor<IDLTypeDeclaration>() {
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
