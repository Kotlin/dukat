package org.jetbrains.dukat.idlParser.visitors

import org.antlr.v4.runtime.tree.TerminalNode
import org.antlr.webidl.WebIDLBaseVisitor
import org.antlr.webidl.WebIDLParser
import org.jetbrains.dukat.idlDeclarations.IDLTypeDeclaration
import org.jetbrains.dukat.idlParser.getFirstValueOrNull
import org.jetbrains.dukat.idlParser.getName
import org.jetbrains.dukat.idlParser.getNameOrNull

internal class TypeVisitor : WebIDLBaseVisitor<IDLTypeDeclaration>() {
    private var name: String = ""
    private var typeParameter: IDLTypeDeclaration? = null
    private var nullable: Boolean = false

    override fun defaultResult(): IDLTypeDeclaration {
        return IDLTypeDeclaration(
                name = name,
                typeParameter = typeParameter,
                nullable = nullable
        )
    }

    override fun visitSingleType(ctx: WebIDLParser.SingleTypeContext): IDLTypeDeclaration {
        ctx.getFirstValueOrNull()?.let { name = it }
        visitChildren(ctx)
        return defaultResult()
    }

    override fun visitReturnType(ctx: WebIDLParser.ReturnTypeContext): IDLTypeDeclaration {
        ctx.getFirstValueOrNull()?.let { name = it }
        visitChildren(ctx)
        return defaultResult()
    }

    override fun visitConstType(ctx: WebIDLParser.ConstTypeContext): IDLTypeDeclaration {
        ctx.getFirstValueOrNull()?.let { name = it }
        visitChildren(ctx)
        return defaultResult()
    }

    override fun visitNonAnyType(ctx: WebIDLParser.NonAnyTypeContext): IDLTypeDeclaration {
        ctx.getFirstValueOrNull()?.let { name = it }

        //if this type is parametrized
        if (ctx.children?.getOrNull(1)?.text == "<") {

            //create new visitor for type parameter
            typeParameter = TypeVisitor().visit(ctx.children?.getOrNull(2))

            //visit suffix
            visit(ctx.children?.getOrNull(4))
        } else {
            visitChildren(ctx)
        }
        return defaultResult()
    }

    override fun visitPrimitiveType(ctx: WebIDLParser.PrimitiveTypeContext): IDLTypeDeclaration {
        name = ctx.text
        return defaultResult()
    }

    override fun visitSequenceType(ctx: WebIDLParser.SequenceTypeContext): IDLTypeDeclaration {
        name = ctx.getFirstValueOrNull()!!
        typeParameter = TypeVisitor().visitChildren(ctx)
        return defaultResult()
    }

    override fun visitPromiseType(ctx: WebIDLParser.PromiseTypeContext): IDLTypeDeclaration {
        name = ctx.getFirstValueOrNull()!!
        typeParameter = TypeVisitor().visitChildren(ctx)
        return defaultResult()
    }

    override fun visitTypeSuffix(ctx: WebIDLParser.TypeSuffixContext): IDLTypeDeclaration {
        val suffix = ctx.getFirstValueOrNull()
        if (suffix == "?") {
            nullable = true
        } else if (suffix == "[") {
            typeParameter = IDLTypeDeclaration(name, typeParameter, nullable)
            name = "\$Array"
            nullable = false
        }
        visitChildren(ctx)
        return defaultResult()
    }

    override fun visitTypeSuffixStartingWithArray(ctx: WebIDLParser.TypeSuffixStartingWithArrayContext): IDLTypeDeclaration {
        val suffix = ctx.getFirstValueOrNull()
        if (suffix == "[") {
            typeParameter = IDLTypeDeclaration(name, typeParameter, nullable)
            name = "\$Array"
            nullable = false
        }
        visitChildren(ctx)
        return defaultResult()
    }

    override fun visitNull_(ctx: WebIDLParser.Null_Context): IDLTypeDeclaration {
        val suffix = ctx.getFirstValueOrNull()
        if (suffix == "?") {
            nullable = true
        }
        return defaultResult()
    }
}
