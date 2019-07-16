package org.jetbrains.dukat.idlParser.visitors

import org.antlr.webidl.WebIDLBaseVisitor
import org.antlr.webidl.WebIDLParser
import org.jetbrains.dukat.idlDeclarations.IDLArgumentDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLOperationDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLTypeDeclaration

internal class OperationVisitor : WebIDLBaseVisitor<IDLOperationDeclaration>() {

    private var name : String = ""
    private var type : IDLTypeDeclaration = IDLTypeDeclaration("")
    private val arguments: MutableList<IDLArgumentDeclaration> = mutableListOf()

    override fun defaultResult() = IDLOperationDeclaration(name, type, arguments)

    override fun visitReturnType(ctx: WebIDLParser.ReturnTypeContext?): IDLOperationDeclaration {
        type = TypeVisitor().visit(ctx)
        return defaultResult()
    }

    override fun visitOptionalIdentifier(ctx: WebIDLParser.OptionalIdentifierContext): IDLOperationDeclaration {
        name = ctx.text
        return defaultResult()
    }

    override fun visitOptionalOrRequiredArgument(ctx: WebIDLParser.OptionalOrRequiredArgumentContext?): IDLOperationDeclaration {
        arguments.add(ArgumentVisitor().visit(ctx))
        return defaultResult()
    }

}