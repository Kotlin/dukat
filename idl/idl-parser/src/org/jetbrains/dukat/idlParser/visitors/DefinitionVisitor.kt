package org.jetbrains.dukat.idlParser.visitors

import org.antlr.webidl.WebIDLBaseVisitor
import org.antlr.webidl.WebIDLParser
import org.jetbrains.dukat.idlDeclarations.IDLAttributeDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLInterfaceDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLOperationDeclaration
import org.jetbrains.dukat.idlParser.getName

internal class DefinitionVisitor : WebIDLBaseVisitor<IDLDeclaration>() {
    private var name: String = ""
    private val attributes: MutableList<IDLAttributeDeclaration> = mutableListOf()
    private val operations: MutableList<IDLOperationDeclaration> = mutableListOf()

    override fun defaultResult(): IDLDeclaration {
        return IDLInterfaceDeclaration(name, attributes, operations)
    }

    override fun visitAttributeRest(ctx: WebIDLParser.AttributeRestContext): IDLDeclaration {
        with (AttributeVisitor()) {
            visit(ctx)
            this@DefinitionVisitor.attributes.add(visitAttributeRest(ctx))
        }
        return defaultResult()
    }

    override fun visitInterface_(ctx: WebIDLParser.Interface_Context): IDLDeclaration {
        name = getName(ctx)
        visitChildren(ctx)
        return defaultResult()
    }

    override fun visitOperation(ctx: WebIDLParser.OperationContext): IDLDeclaration {
        operations.add(OperationVisitor().visit(ctx))
        return defaultResult()
    }
}
