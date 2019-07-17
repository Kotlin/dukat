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

    override fun defaultResult(): IDLTopLevelDeclaration {
        return IDLInterfaceDeclaration(
                name = name,
                constants = constants,
                attributes = myAttributes,
                operations = operations,
                primaryConstructor = null,
                constructors = listOf(),
                parents = parents,
                extendedAttributes = extendedAttributes
        )
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
}
