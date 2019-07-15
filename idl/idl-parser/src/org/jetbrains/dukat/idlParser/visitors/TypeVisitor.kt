package org.jetbrains.dukat.idlParser.visitors

import org.antlr.webidl.WebIDLBaseVisitor
import org.antlr.webidl.WebIDLParser
import org.jetbrains.dukat.idlDeclarations.IDLTypeDeclaration
import org.jetbrains.dukat.idlParser.getName
import org.jetbrains.dukat.idlParser.getNameOrNull

internal class TypeVisitor : WebIDLBaseVisitor<IDLTypeDeclaration>() {
    private var type: IDLTypeDeclaration = IDLTypeDeclaration("")

    override fun visitNonAnyType(ctx: WebIDLParser.NonAnyTypeContext): IDLTypeDeclaration {
        val name : String? = ctx.getNameOrNull()
        if (name != null) {
            type = IDLTypeDeclaration(name)
        }
        visitChildren(ctx)
        return type
    }

    override fun visitType(ctx: WebIDLParser.TypeContext?): IDLTypeDeclaration {
        super.visitType(ctx)
        return type
    }

    override fun visitPrimitiveType(ctx: WebIDLParser.PrimitiveTypeContext): IDLTypeDeclaration {
        type = IDLTypeDeclaration(ctx.text)
        return type
    }
}
