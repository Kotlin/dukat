package org.jetbrains.dukat.idlParser.visitors

import org.antlr.v4.runtime.tree.TerminalNode
import org.antlr.webidl.WebIDLBaseVisitor
import org.antlr.webidl.WebIDLLexer
import org.antlr.webidl.WebIDLParser
import org.jetbrains.dukat.idlDeclarations.IDLAttributeDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLDictionaryDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLDictionaryMemberDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLEnumDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLExtendedAttributeDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLGetterDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLImplementsStatementDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLIncludesStatementDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLInterfaceDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLNamespaceDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLOperationDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLSetterDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLSingleTypeDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLTopLevelDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLTypeDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLTypedefDeclaration
import org.jetbrains.dukat.idlDeclarations.InterfaceKind
import org.jetbrains.dukat.idlParser.filterIdentifiers
import org.jetbrains.dukat.idlParser.getFirstValueOrNull
import org.jetbrains.dukat.idlParser.getName

internal class DefinitionVisitor(private val extendedAttributes: List<IDLExtendedAttributeDeclaration>) :
        WebIDLBaseVisitor<IDLTopLevelDeclaration>() {

    private var name: String = ""
    private val myAttributes: MutableList<IDLAttributeDeclaration> = mutableListOf()
    private val operations: MutableList<IDLOperationDeclaration> = mutableListOf()
    private val parents: MutableList<IDLSingleTypeDeclaration> = mutableListOf()
    private var typeReference: IDLTypeDeclaration = IDLSingleTypeDeclaration("", null, false)
    private var childType: IDLTypeDeclaration = IDLSingleTypeDeclaration("", null, false)
    private var parentType: IDLTypeDeclaration = IDLSingleTypeDeclaration("", null, false)
    private val getters: MutableList<IDLGetterDeclaration> = mutableListOf()
    private val setters: MutableList<IDLSetterDeclaration> = mutableListOf()
    private val dictionaryMembers: MutableList<IDLDictionaryMemberDeclaration> = mutableListOf()
    private val enumMembers: MutableList<String> = mutableListOf()
    private var isCallback: Boolean = false
    private var partial = false
    private var mixin = false

    private var kind: DefinitionKind = DefinitionKind.INTERFACE

    override fun defaultResult(): IDLTopLevelDeclaration {
        return when (kind) {
            DefinitionKind.INTERFACE -> IDLInterfaceDeclaration(
                    name = name,
                    attributes = myAttributes,
                    operations = operations,
                    primaryConstructor = null,
                    constructors = listOf(),
                    parents = parents,
                    unions = listOf(),
                    extendedAttributes = extendedAttributes,
                    getters = getters,
                    setters = setters,
                    callback = isCallback,
                    generated = false,
                    partial = partial,
                    mixin = mixin,
                    kind = InterfaceKind.INTERFACE
            )
            DefinitionKind.TYPEDEF -> IDLTypedefDeclaration(
                    name = name,
                    typeReference = typeReference
            )
            DefinitionKind.DICTIONARY -> IDLDictionaryDeclaration(
                    name = name,
                    members = dictionaryMembers,
                    parents = parents,
                    unions = listOf(),
                    partial = partial
            )
            DefinitionKind.IMPLEMENTS_STATEMENT -> IDLImplementsStatementDeclaration(
                    child = childType,
                    parent = parentType
            )
            DefinitionKind.INCLUDES_STATEMENT -> IDLIncludesStatementDeclaration(
                    child = childType,
                    parent = parentType
            )
            DefinitionKind.ENUM -> IDLEnumDeclaration(
                    name = name,
                    members = enumMembers,
                    unions = listOf()
            )
            DefinitionKind.NAMESPACE -> IDLNamespaceDeclaration(
                    name = name,
                    attributes = myAttributes,
                    operations = operations,
                    partial = partial
            )
        }
    }

    override fun visitAttributeRest(ctx: WebIDLParser.AttributeRestContext): IDLTopLevelDeclaration {
        myAttributes.add(MemberVisitor().visit(ctx) as IDLAttributeDeclaration)
        return defaultResult()
    }

    override fun visitReadonlyMember(ctx: WebIDLParser.ReadonlyMemberContext?): IDLTopLevelDeclaration {
        when (val readOnlyMember = MemberVisitor().visit(ctx)) {
            is IDLAttributeDeclaration -> myAttributes.add(readOnlyMember)
        }
        return defaultResult()
    }

    override fun visitConst_(ctx: WebIDLParser.Const_Context?): IDLTopLevelDeclaration {
        myAttributes.add(MemberVisitor().visit(ctx) as IDLAttributeDeclaration)
        return defaultResult()
    }

    override fun visitInterfaceRest(ctx: WebIDLParser.InterfaceRestContext): IDLTopLevelDeclaration {
        kind = DefinitionKind.INTERFACE
        name = ctx.getName()
        visitChildren(ctx)
        return defaultResult()
    }

    override fun visitMixinRest(ctx: WebIDLParser.MixinRestContext): IDLTopLevelDeclaration {
        kind = DefinitionKind.INTERFACE
        mixin = true
        name = ctx.getName()
        visitChildren(ctx)
        return defaultResult()
    }

    override fun visitPartialInterfaceRest(ctx: WebIDLParser.PartialInterfaceRestContext): IDLTopLevelDeclaration {
        kind = DefinitionKind.INTERFACE
        name = ctx.getName()
        visitChildren(ctx)
        return defaultResult()
    }

    override fun visitStaticMember(ctx: WebIDLParser.StaticMemberContext?): IDLTopLevelDeclaration {
        when (val staticMember = MemberVisitor().visit(ctx)) {
            is IDLOperationDeclaration -> operations.add(staticMember)
            is IDLAttributeDeclaration -> myAttributes.add(staticMember)
        }
        return defaultResult()
    }

    override fun visitRegularOperation(ctx: WebIDLParser.RegularOperationContext): IDLTopLevelDeclaration {
        operations.add(MemberVisitor().visit(ctx) as IDLOperationDeclaration)
        return defaultResult()
    }

    override fun visitStringifier(ctx: WebIDLParser.StringifierContext): IDLTopLevelDeclaration {
        when (val stringifier = MemberVisitor().visit(ctx)) {
            is IDLOperationDeclaration -> operations.add(stringifier)
            is IDLAttributeDeclaration -> myAttributes.add(stringifier)
        }
        return defaultResult()
    }

    override fun visitOperation(ctx: WebIDLParser.OperationContext): IDLTopLevelDeclaration {
        when (val operation = MemberVisitor().visit(ctx)) {
            is IDLGetterDeclaration -> getters.add(operation)
            is IDLSetterDeclaration -> setters.add(operation)
            is IDLOperationDeclaration -> operations.add(operation)
        }
        return defaultResult()
    }

    override fun visitInheritance(ctx: WebIDLParser.InheritanceContext): IDLTopLevelDeclaration {
        parents.addAll(ctx.filterIdentifiers().map { IDLSingleTypeDeclaration(it.text, null, false) })
        return defaultResult()
    }

    override fun visitTypedef(ctx: WebIDLParser.TypedefContext): IDLTopLevelDeclaration {
        kind = DefinitionKind.TYPEDEF
        name = ctx.getName()
        visitChildren(ctx)
        return defaultResult()
    }

    override fun visitType(ctx: WebIDLParser.TypeContext): IDLTopLevelDeclaration {
        typeReference = TypeVisitor().visit(ctx)
        return defaultResult()
    }

    override fun visitCallbackRestOrInterface(ctx: WebIDLParser.CallbackRestOrInterfaceContext): IDLTopLevelDeclaration {
        isCallback = true
        if (ctx.getFirstValueOrNull() == "interface") {
            kind = DefinitionKind.INTERFACE
            name = ctx.getName()
        }
        visitChildren(ctx)
        return defaultResult()
    }

    override fun visitImplementsStatement(ctx: WebIDLParser.ImplementsStatementContext): IDLTopLevelDeclaration {
        kind = DefinitionKind.IMPLEMENTS_STATEMENT
        val identifiers = ctx.filterIdentifiers()
        childType = IDLSingleTypeDeclaration(identifiers[0].text, null, false)
        parentType = IDLSingleTypeDeclaration(identifiers[1].text, null, false)
        return defaultResult()
    }

    override fun visitIncludesStatement(ctx: WebIDLParser.IncludesStatementContext): IDLTopLevelDeclaration {
        kind = DefinitionKind.INCLUDES_STATEMENT
        val identifiers = ctx.filterIdentifiers()
        childType = IDLSingleTypeDeclaration(identifiers[0].text, null, false)
        parentType = IDLSingleTypeDeclaration(identifiers[1].text, null, false)
        return defaultResult()
    }

    override fun visitDictionary(ctx: WebIDLParser.DictionaryContext): IDLTopLevelDeclaration {
        kind = DefinitionKind.DICTIONARY
        name = ctx.getName()
        visitChildren(ctx)
        return defaultResult()
    }

    override fun visitPartialDictionary(ctx: WebIDLParser.PartialDictionaryContext): IDLTopLevelDeclaration {
        kind = DefinitionKind.DICTIONARY
        name = ctx.getName()
        visitChildren(ctx)
        return defaultResult()
    }

    override fun visitDictionaryMember(ctx: WebIDLParser.DictionaryMemberContext): IDLTopLevelDeclaration {
        dictionaryMembers.add(MemberVisitor().visit(ctx) as IDLDictionaryMemberDeclaration)
        return defaultResult()
    }

    override fun visitCallbackRest(ctx: WebIDLParser.CallbackRestContext): IDLTopLevelDeclaration {
        kind = DefinitionKind.TYPEDEF
        name = ctx.getName()
        typeReference = TypeVisitor().visit(ctx)
        return defaultResult()
    }

    override fun visitEnum_(ctx: WebIDLParser.Enum_Context): IDLTopLevelDeclaration {
        kind = DefinitionKind.ENUM
        name = ctx.getName()
        visitChildren(ctx)
        return defaultResult()
    }

    override fun visitEnumValueList(ctx: WebIDLParser.EnumValueListContext): IDLTopLevelDeclaration {
        object : WebIDLBaseVisitor<Unit>() {
            override fun visitTerminal(node: TerminalNode) {
                if (node.symbol.type == WebIDLLexer.STRING_WEBIDL) {
                    enumMembers.add(node.text)
                }
            }
        }.visit(ctx)
        return defaultResult()
    }

    override fun visitNamespace(ctx: WebIDLParser.NamespaceContext): IDLTopLevelDeclaration {
        kind = DefinitionKind.NAMESPACE
        name = ctx.getName()
        visitChildren(ctx)
        return defaultResult()
    }

    override fun visitNamespaceMember(ctx: WebIDLParser.NamespaceMemberContext): IDLTopLevelDeclaration {
        when (val member = MemberVisitor().visit(ctx)) {
            is IDLAttributeDeclaration -> myAttributes.add(member)
            is IDLOperationDeclaration -> operations.add(member)
        }
        return defaultResult()
    }

    override fun visitMixinMember(ctx: WebIDLParser.MixinMemberContext): IDLTopLevelDeclaration {
        when (val member = MemberVisitor().visit(ctx)) {
            is IDLAttributeDeclaration -> myAttributes.add(member)
            is IDLOperationDeclaration -> operations.add(member)
        }
        return defaultResult()
    }

    override fun visitPartial(ctx: WebIDLParser.PartialContext): IDLTopLevelDeclaration {
        partial = true
        visitChildren(ctx)
        return defaultResult()
    }
}

private enum class DefinitionKind {
    INTERFACE, TYPEDEF, IMPLEMENTS_STATEMENT, INCLUDES_STATEMENT, DICTIONARY, ENUM, NAMESPACE
}