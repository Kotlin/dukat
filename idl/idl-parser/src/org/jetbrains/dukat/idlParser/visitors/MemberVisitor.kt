package org.jetbrains.dukat.idlParser.visitors

import org.antlr.v4.runtime.tree.TerminalNode
import org.antlr.webidl.WebIDLBaseVisitor
import org.antlr.webidl.WebIDLParser
import org.jetbrains.dukat.idlDeclarations.*
import org.jetbrains.dukat.idlParser.getName
import org.jetbrains.dukat.idlParser.getNameOrNull

internal class MemberVisitor : WebIDLBaseVisitor<IDLMemberDeclaration>() {
    private var kind: MemberKind = MemberKind.ATTRIBUTE

    private var name : String = ""
    private var type : IDLTypeDeclaration = IDLTypeDeclaration("", null, false)
    private val arguments: MutableList<IDLArgumentDeclaration> = mutableListOf()
    private var constValue: String? = null
    private var static: Boolean = false
    private var readOnly: Boolean = false

    override fun defaultResult(): IDLMemberDeclaration {
        return when (kind) {
            MemberKind.OPERATION -> IDLOperationDeclaration(name, type, arguments, static)
            MemberKind.ATTRIBUTE -> IDLAttributeDeclaration(name, type, static, readOnly)
            MemberKind.CONSTANT -> IDLAttributeDeclaration(name, type, static = true, readOnly = true)
            MemberKind.DICTIONARY_MEMBER -> IDLDictionaryMemberDeclaration(name, type, constValue)
        }
    }

    override fun visitOperationRest(ctx: WebIDLParser.OperationRestContext?): IDLMemberDeclaration {
        kind = MemberKind.OPERATION
        visitChildren(ctx)
        return defaultResult()
    }

    override fun visitAttributeRest(ctx: WebIDLParser.AttributeRestContext): IDLMemberDeclaration {
        kind = MemberKind.ATTRIBUTE
        name = ctx.getNameOrNull() ?: ctx.children.filterIsInstance<TerminalNode>().last { it.text != ";" }.text
        visitChildren(ctx)
        return defaultResult()
    }

    override fun visitConst_(ctx: WebIDLParser.Const_Context): IDLMemberDeclaration {
        kind = MemberKind.CONSTANT
        name = ctx.getName()
        visitChildren(ctx)
        return defaultResult()
    }

    override fun visitDictionaryMember(ctx: WebIDLParser.DictionaryMemberContext): IDLMemberDeclaration {
        kind = MemberKind.DICTIONARY_MEMBER
        name = ctx.getName()
        visitChildren(ctx)
        return defaultResult()
    }

    override fun visitReturnType(ctx: WebIDLParser.ReturnTypeContext?): IDLMemberDeclaration {
        type = TypeVisitor().visit(ctx)
        return defaultResult()
    }

    override fun visitOptionalIdentifier(ctx: WebIDLParser.OptionalIdentifierContext): IDLMemberDeclaration {
        name = ctx.text
        return defaultResult()
    }

    override fun visitOptionalOrRequiredArgument(ctx: WebIDLParser.OptionalOrRequiredArgumentContext?): IDLMemberDeclaration {
        arguments.add(ArgumentVisitor().visit(ctx))
        return defaultResult()
    }

    override fun visitType(ctx: WebIDLParser.TypeContext): IDLMemberDeclaration {
        type = TypeVisitor().visit(ctx)
        return defaultResult()
    }

    override fun visitConstType(ctx: WebIDLParser.ConstTypeContext): IDLMemberDeclaration {
        type = TypeVisitor().visit(ctx)
        return defaultResult()
    }

    override fun visitConstValue(ctx: WebIDLParser.ConstValueContext): IDLMemberDeclaration {
        constValue = object : WebIDLBaseVisitor<String>() {
            override fun visitTerminal(node: TerminalNode): String {
                return node.text
            }
        }.visit(ctx)
        return defaultResult()
    }

    override fun visitStaticMemberRest(ctx: WebIDLParser.StaticMemberRestContext?): IDLMemberDeclaration {
        static = true
        visitChildren(ctx)
        return defaultResult()
    }

    override fun visitReadonlyMemberRest(ctx: WebIDLParser.ReadonlyMemberRestContext?): IDLMemberDeclaration {
        readOnly = true
        visitChildren(ctx)
        return defaultResult()
    }

    override fun visitReadOnly(ctx: WebIDLParser.ReadOnlyContext): IDLMemberDeclaration {
        if (ctx.text.isNotEmpty()) {
            readOnly = true
        }
        return defaultResult()
    }

}

private enum class MemberKind {
    OPERATION, ATTRIBUTE, CONSTANT, DICTIONARY_MEMBER
}