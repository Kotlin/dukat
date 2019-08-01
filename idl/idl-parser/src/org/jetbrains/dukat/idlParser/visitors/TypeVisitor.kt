package org.jetbrains.dukat.idlParser.visitors

import org.antlr.webidl.WebIDLBaseVisitor
import org.antlr.webidl.WebIDLParser
import org.jetbrains.dukat.idlDeclarations.IDLArgumentDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLFunctionTypeDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLSingleTypeDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLTypeDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLUnionTypeDeclaration
import org.jetbrains.dukat.idlParser.getFirstValueOrNull
import org.jetbrains.dukat.idlParser.getName

internal class TypeVisitor(private var name: String = "",
                           private var typeParameter: IDLTypeDeclaration? = null
) : WebIDLBaseVisitor<IDLTypeDeclaration>() {
    private var nullable: Boolean = false
    private var unionMembers: MutableList<IDLTypeDeclaration> = mutableListOf()
    private var returnType: IDLTypeDeclaration = IDLSingleTypeDeclaration("", null, false)
    private var arguments: MutableList<IDLArgumentDeclaration> = mutableListOf()
    private var kind: TypeKind = TypeKind.SINGLE

    override fun defaultResult(): IDLTypeDeclaration {
        return when (kind) {
            TypeKind.SINGLE -> IDLSingleTypeDeclaration(
                    name = name,
                    typeParameter = typeParameter,
                    nullable = nullable
            )
            TypeKind.UNION -> IDLUnionTypeDeclaration(
                    name = unionMembers.sortedBy { it.name }.joinToString(
                            separator = "Or",
                            prefix = "Union"
                    ) { it.name },
                    unionMembers = unionMembers,
                    nullable = nullable
            )
            TypeKind.FUNCTION -> IDLFunctionTypeDeclaration(
                    name = name,
                    returnType = returnType,
                    arguments = arguments.map { it.copy(name = "") },
                    nullable = nullable
            )
        }
    }

    override fun visitSingleType(ctx: WebIDLParser.SingleTypeContext): IDLTypeDeclaration {
        kind = TypeKind.SINGLE
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
        kind = TypeKind.SINGLE
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
            typeParameter = defaultResult()
            name = "\$Array"
            nullable = false
            kind = TypeKind.SINGLE
        }
        visitChildren(ctx)
        return defaultResult()
    }

    override fun visitTypeSuffixStartingWithArray(ctx: WebIDLParser.TypeSuffixStartingWithArrayContext): IDLTypeDeclaration {
        val suffix = ctx.getFirstValueOrNull()
        if (suffix == "[") {
            typeParameter = defaultResult()
            name = "\$Array"
            nullable = false
            kind = TypeKind.SINGLE
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

    override fun visitUnionType(ctx: WebIDLParser.UnionTypeContext): IDLTypeDeclaration {
        kind = TypeKind.UNION
        visitChildren(ctx)
        return defaultResult()
    }

    override fun visitUnionMemberType(ctx: WebIDLParser.UnionMemberTypeContext): IDLTypeDeclaration {
        if (ctx.getFirstValueOrNull() == "any") {
            //array of any's
            unionMembers.add(TypeVisitor(
                    name = "\$Array",
                    typeParameter = IDLSingleTypeDeclaration(
                            "any",
                            null,
                            false
                    )
            ).visitChildren(ctx))
        }
        unionMembers.add(TypeVisitor().visitChildren(ctx))
        return defaultResult()
    }

    override fun visitCallbackRest(ctx: WebIDLParser.CallbackRestContext): IDLTypeDeclaration {
        kind = TypeKind.FUNCTION
        name = ctx.getName()
        //visit return type
        returnType = TypeVisitor().visit(ctx.children?.getOrNull(2))
        //visit argument list
        visit(ctx.children?.getOrNull(4))
        return defaultResult()
    }

    override fun visitArgument(ctx: WebIDLParser.ArgumentContext?): IDLTypeDeclaration {
        arguments.add(ArgumentVisitor().visit(ctx))
        return defaultResult()
    }
}

private enum class TypeKind {
    SINGLE, UNION, FUNCTION
}
