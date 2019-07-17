package org.jetbrains.dukat.idlParser.visitors

import org.antlr.webidl.WebIDLBaseVisitor
import org.antlr.webidl.WebIDLParser
import org.jetbrains.dukat.idlDeclarations.*
import org.jetbrains.dukat.idlParser.filterIdentifiers
import org.jetbrains.dukat.idlParser.getName

internal class DefinitionVisitor(private val extendedAttributes: List<IDLExtendedAttributeDeclaration>) :
        WebIDLBaseVisitor<IDLTopLevelDeclaration>() {

    private var name: String = ""
    private val myAttributes: MutableList<IDLAttributeDeclaration> = mutableListOf()
    private val operations: MutableList<IDLOperationDeclaration> = mutableListOf()
    private val parents: MutableList<IDLTypeDeclaration> = mutableListOf()
    private val constants: MutableList<IDLConstantDeclaration> = mutableListOf()
    private var typeReference: IDLTypeDeclaration = IDLTypeDeclaration("")
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
                    extendedAttributes = extendedAttributes
            )
            DefinitionKind.TYPEDEF -> IDLTypedefDeclaration(
                    name,
                    typeReference
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
        parents.addAll(ctx.filterIdentifiers().map { IDLTypeDeclaration(it.text) })
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
}

private enum class DefinitionKind {
    INTERFACE, TYPEDEF
}