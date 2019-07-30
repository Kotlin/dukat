package org.jetbrains.dukat.idlParser.visitors

import org.antlr.webidl.WebIDLBaseVisitor
import org.antlr.webidl.WebIDLParser
import org.jetbrains.dukat.idlDeclarations.IDLAttributeDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLConstantDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLDictionaryDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLDictionaryMemberDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLExtendedAttributeDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLImplementsStatementDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLInterfaceDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLOperationDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLTopLevelDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLTypeDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLTypedefDeclaration
import org.jetbrains.dukat.idlParser.filterIdentifiers
import org.jetbrains.dukat.idlParser.getName

internal class DefinitionVisitor(private val extendedAttributes: List<IDLExtendedAttributeDeclaration>) :
        WebIDLBaseVisitor<IDLTopLevelDeclaration>() {

    private var name: String = ""
    private val myAttributes: MutableList<IDLAttributeDeclaration> = mutableListOf()
    private val operations: MutableList<IDLOperationDeclaration> = mutableListOf()
    private val parents: MutableList<IDLTypeDeclaration> = mutableListOf()
    private val constants: MutableList<IDLConstantDeclaration> = mutableListOf()
    private var typeReference: IDLTypeDeclaration = IDLTypeDeclaration("", null, false)
    private var childType: IDLTypeDeclaration = IDLTypeDeclaration("", null, false)
    private var parentType: IDLTypeDeclaration = IDLTypeDeclaration("", null, false)
    private var dictionaryMembers: MutableList<IDLDictionaryMemberDeclaration> = mutableListOf()
    private var partial = false

    private var kind: DefinitionKind = DefinitionKind.INTERFACE

    override fun defaultResult(): IDLTopLevelDeclaration {
        return when (kind) {
            DefinitionKind.INTERFACE -> IDLInterfaceDeclaration(
                    name = name,
                    attributes = myAttributes,
                    constants = constants,
                    operations = operations,
                    primaryConstructor = null,
                    constructors = listOf(),
                    parents = parents,
                    extendedAttributes = extendedAttributes,
                    partial = partial
            )
            DefinitionKind.TYPEDEF -> IDLTypedefDeclaration(
                    name = name,
                    typeReference = typeReference
            )
            DefinitionKind.DICTIONARY -> IDLDictionaryDeclaration(
                    name = name,
                    members = dictionaryMembers,
                    parents = parents,
                    partial = partial
            )
            DefinitionKind.IMPLEMENTS_STATEMENT -> IDLImplementsStatementDeclaration(
                    child = childType,
                    parent = parentType
            )
        }
    }

    override fun visitReadWriteAttribute(ctx: WebIDLParser.ReadWriteAttributeContext): IDLTopLevelDeclaration {
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
        constants.add(MemberVisitor().visit(ctx) as IDLConstantDeclaration)
        return defaultResult()
    }

    override fun visitInterface_(ctx: WebIDLParser.Interface_Context): IDLTopLevelDeclaration {
        kind = DefinitionKind.INTERFACE
        name = ctx.getName()
        visitChildren(ctx)
        return defaultResult()
    }

    override fun visitPartialInterface(ctx: WebIDLParser.PartialInterfaceContext): IDLTopLevelDeclaration {
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

    override fun visitOperation(ctx: WebIDLParser.OperationContext): IDLTopLevelDeclaration {
        operations.add(MemberVisitor().visit(ctx) as IDLOperationDeclaration)
        return defaultResult()
    }

    override fun visitInheritance(ctx: WebIDLParser.InheritanceContext): IDLTopLevelDeclaration {
        parents.addAll(ctx.filterIdentifiers().map { IDLTypeDeclaration(it.text, null, false) })
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

    override fun visitImplementsStatement(ctx: WebIDLParser.ImplementsStatementContext): IDLTopLevelDeclaration {
        kind = DefinitionKind.IMPLEMENTS_STATEMENT
        val identifiers = ctx.filterIdentifiers()
        childType = IDLTypeDeclaration(identifiers[0].text, null, false)
        parentType = IDLTypeDeclaration(identifiers[1].text, null, false)
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

    override fun visitPartial(ctx: WebIDLParser.PartialContext): IDLTopLevelDeclaration {
        partial = true
        visitChildren(ctx)
        return defaultResult()
    }
}

private enum class DefinitionKind {
    INTERFACE, TYPEDEF, IMPLEMENTS_STATEMENT, DICTIONARY
}