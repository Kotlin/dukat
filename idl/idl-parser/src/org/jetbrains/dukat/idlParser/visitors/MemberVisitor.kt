package org.jetbrains.dukat.idlParser.visitors

import org.antlr.v4.runtime.tree.TerminalNode
import org.antlr.webidl.WebIDLBaseVisitor
import org.antlr.webidl.WebIDLParser
import org.jetbrains.dukat.idlDeclarations.IDLArgumentDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLAttributeDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLDictionaryMemberDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLGetterDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLMemberDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLOperationDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLSetterDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLSingleTypeDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLTypeDeclaration
import org.jetbrains.dukat.idlParser.getFirstValueOrNull
import org.jetbrains.dukat.idlParser.getName
import org.jetbrains.dukat.idlParser.getNameOrNull

internal class MemberVisitor : WebIDLBaseVisitor<IDLMemberDeclaration?>() {
    private var kind: MemberKind = MemberKind.EMPTY

    private var name: String = ""
    private var type: IDLTypeDeclaration = IDLSingleTypeDeclaration("", null, false)
    private val arguments: MutableList<IDLArgumentDeclaration> = mutableListOf()
    private var constValue: String? = null
    private var static: Boolean = false
    private var readOnly: Boolean = false
    private var required: Boolean = false

    override fun defaultResult(): IDLMemberDeclaration? {
        return when (kind) {
            MemberKind.OPERATION -> if (name != "") {
                IDLOperationDeclaration(name, type, arguments, static, override = false)
            } else {
                null
            }
            MemberKind.ATTRIBUTE -> IDLAttributeDeclaration(name, type, static, readOnly, override = false)
            MemberKind.CONSTANT -> IDLAttributeDeclaration(name, type, static = true, readOnly = true, override = false)
            MemberKind.DICTIONARY_MEMBER -> IDLDictionaryMemberDeclaration(name, type, constValue, required)
            MemberKind.GETTER -> IDLGetterDeclaration(
                    name.ifEmpty { "get" },
                    arguments.getOrElse(0) { IDLArgumentDeclaration("", IDLSingleTypeDeclaration("", null, false), null, false, false) },
                    type
            )
            MemberKind.SETTER -> IDLSetterDeclaration(
                    name.ifEmpty { "set" },
                    arguments.getOrElse(0) { IDLArgumentDeclaration("", IDLSingleTypeDeclaration("", null, false), null, false, false) },
                    arguments.getOrElse(1) { IDLArgumentDeclaration("", IDLSingleTypeDeclaration("", null, false), null, false, false) }
            )
            MemberKind.EMPTY -> null
        }
    }

    override fun visitOperationRest(ctx: WebIDLParser.OperationRestContext?): IDLMemberDeclaration? {
        if (kind != MemberKind.SETTER && kind != MemberKind.GETTER) {
            kind = MemberKind.OPERATION
        }
        visitChildren(ctx)
        return defaultResult()
    }

    override fun visitAttributeRest(ctx: WebIDLParser.AttributeRestContext): IDLMemberDeclaration? {
        kind = MemberKind.ATTRIBUTE
        visitChildren(ctx)
        return defaultResult()
    }

    override fun visitAttributeName(ctx: WebIDLParser.AttributeNameContext): IDLMemberDeclaration? {
        name = ctx.getNameOrNull() ?: "required"
        return defaultResult()
    }

    override fun visitConst_(ctx: WebIDLParser.Const_Context): IDLMemberDeclaration? {
        kind = MemberKind.CONSTANT
        name = ctx.getName()
        visitChildren(ctx)
        return defaultResult()
    }

    override fun visitDictionaryMemberRest(ctx: WebIDLParser.DictionaryMemberRestContext): IDLMemberDeclaration? {
        if (ctx.getFirstValueOrNull() == "required") {
            required = true
        }
        kind = MemberKind.DICTIONARY_MEMBER
        name = ctx.getName()
        visitChildren(ctx)
        return defaultResult()
    }

    override fun visitReturnType(ctx: WebIDLParser.ReturnTypeContext?): IDLMemberDeclaration? {
        type = TypeVisitor().visit(ctx)
        return defaultResult()
    }

    override fun visitOptionalIdentifier(ctx: WebIDLParser.OptionalIdentifierContext): IDLMemberDeclaration? {
        name = ctx.text
        return defaultResult()
    }

    override fun visitArgumentRest(ctx: WebIDLParser.ArgumentRestContext?): IDLMemberDeclaration? {
        arguments.add(ArgumentVisitor().visit(ctx))
        return defaultResult()
    }

    override fun visitType(ctx: WebIDLParser.TypeContext): IDLMemberDeclaration? {
        type = TypeVisitor().visit(ctx)
        return defaultResult()
    }

    override fun visitConstType(ctx: WebIDLParser.ConstTypeContext): IDLMemberDeclaration? {
        type = TypeVisitor().visit(ctx)
        return defaultResult()
    }

    override fun visitConstValue(ctx: WebIDLParser.ConstValueContext): IDLMemberDeclaration? {
        constValue = object : WebIDLBaseVisitor<String>() {
            override fun visitTerminal(node: TerminalNode): String {
                return node.text
            }
        }.visit(ctx)
        return defaultResult()
    }

    override fun visitDefaultValue(ctx: WebIDLParser.DefaultValueContext): IDLMemberDeclaration? {
        when (ctx.getFirstValueOrNull()) {
            "[" -> constValue = "[]"
            null -> visitChildren(ctx)
            else -> constValue = ctx.getFirstValueOrNull()
        }
        return defaultResult()
    }

    override fun visitStaticMemberRest(ctx: WebIDLParser.StaticMemberRestContext?): IDLMemberDeclaration? {
        static = true
        visitChildren(ctx)
        return defaultResult()
    }

    override fun visitReadonlyMemberRest(ctx: WebIDLParser.ReadonlyMemberRestContext?): IDLMemberDeclaration? {
        readOnly = true
        visitChildren(ctx)
        return defaultResult()
    }

    override fun visitNamespaceMember(ctx: WebIDLParser.NamespaceMemberContext): IDLMemberDeclaration? {
        if (ctx.getFirstValueOrNull() == "readonly") {
            readOnly = true
        }
        visitChildren(ctx)
        return defaultResult()
    }

    override fun visitReadOnly(ctx: WebIDLParser.ReadOnlyContext): IDLMemberDeclaration? {
        if (ctx.text.isNotEmpty()) {
            readOnly = true
        }
        return defaultResult()
    }

    override fun visitSpecial(ctx: WebIDLParser.SpecialContext): IDLMemberDeclaration? {
        when (ctx.text) {
            "getter" -> kind = MemberKind.GETTER
            "setter" -> kind = MemberKind.SETTER
        }
        return defaultResult()
    }
}

private enum class MemberKind {
    OPERATION, ATTRIBUTE, CONSTANT, DICTIONARY_MEMBER, GETTER, SETTER, EMPTY
}