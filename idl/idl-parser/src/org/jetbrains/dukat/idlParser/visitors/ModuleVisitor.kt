package org.jetbrains.dukat.idlParser.visitors

import org.antlr.webidl.WebIDLBaseVisitor
import org.antlr.webidl.WebIDLParser
import org.jetbrains.dukat.idlDeclarations.IDLExtendedAttributeDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLTopLevelDeclaration

internal class ModuleVisitor(val declarations: MutableList<IDLTopLevelDeclaration>) : WebIDLBaseVisitor<Unit>() {

    private var extendedAttributes: MutableList<IDLExtendedAttributeDeclaration> = mutableListOf()

    override fun visitDefinition(ctx: WebIDLParser.DefinitionContext?) {
        declarations.add(DefinitionVisitor(extendedAttributes).visitDefinition(ctx))
        extendedAttributes = mutableListOf()
    }

    override fun visitExtendedAttribute(ctx: WebIDLParser.ExtendedAttributeContext?) {
        extendedAttributes.add(ExtendedAttributeVisitor().visit(ctx))
    }

}
