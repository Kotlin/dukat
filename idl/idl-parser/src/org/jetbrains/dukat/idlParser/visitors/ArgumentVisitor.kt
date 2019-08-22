package org.jetbrains.dukat.idlParser.visitors

import org.antlr.webidl.WebIDLBaseVisitor
import org.antlr.webidl.WebIDLParser
import org.jetbrains.dukat.idlDeclarations.IDLArgumentDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLSingleTypeDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLTypeDeclaration
import org.jetbrains.dukat.idlParser.getFirstValueOrNull

internal class ArgumentVisitor: WebIDLBaseVisitor<IDLArgumentDeclaration>() {
    private var name: String = ""
    private var type: IDLTypeDeclaration = IDLSingleTypeDeclaration("", null, false)
    private var optional: Boolean = false
    private var variadic: Boolean = false
    private var defaultValue: String? = null

    override fun defaultResult() = IDLArgumentDeclaration(name, type, defaultValue, optional, variadic)

    override fun visitArgumentRest(ctx: WebIDLParser.ArgumentRestContext): IDLArgumentDeclaration {
        if (ctx.getFirstValueOrNull() == "optional") {
            optional = true
        }
        visitChildren(ctx)
        return defaultResult()
    }

    override fun visitType(ctx: WebIDLParser.TypeContext): IDLArgumentDeclaration {
        type = TypeVisitor().visit(ctx)
        return defaultResult()
    }

    override fun visitArgumentName(ctx: WebIDLParser.ArgumentNameContext): IDLArgumentDeclaration {
        name = ctx.text
        return defaultResult()
    }

    override fun visitDefaultValue(ctx: WebIDLParser.DefaultValueContext): IDLArgumentDeclaration {
        defaultValue = ctx.text
        return defaultResult()
    }

    override fun visitEllipsis(ctx: WebIDLParser.EllipsisContext): IDLArgumentDeclaration {
        if (ctx.text == "...") {
            variadic = true
        }
        return defaultResult()
    }
}
