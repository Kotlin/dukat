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

    override fun defaultResult(): IDLTopLevelDeclaration {
        return IDLInterfaceDeclaration(
                name = name,
                attributes = myAttributes,
                operations = operations,
                constructors = listOf(),
                parents = parents,
                extendedAttributes = extendedAttributes
        )
    }

    override fun visitAttributeRest(ctx: WebIDLParser.AttributeRestContext): IDLTopLevelDeclaration {
        myAttributes.add(with(AttributeVisitor()) {
            visit(ctx)
            visitAttributeRest(ctx)
        })
        return defaultResult()
    }

    override fun visitInterface_(ctx: WebIDLParser.Interface_Context): IDLTopLevelDeclaration {
        name = ctx.getName()
        visitChildren(ctx)
        return defaultResult()
    }

    override fun visitOperation(ctx: WebIDLParser.OperationContext): IDLTopLevelDeclaration {
        operations.add(OperationVisitor().visit(ctx))
        return defaultResult()
    }

    override fun visitInheritance(ctx: WebIDLParser.InheritanceContext): IDLTopLevelDeclaration {
        parents.addAll(ctx.filterIdentifiers().map { IDLTypeDeclaration(it.text) })
        return defaultResult()
    }
}
