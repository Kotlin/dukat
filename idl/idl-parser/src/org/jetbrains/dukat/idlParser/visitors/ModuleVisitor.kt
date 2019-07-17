package org.jetbrains.dukat.idlParser.visitors

import org.antlr.webidl.WebIDLBaseVisitor
import org.antlr.webidl.WebIDLParser
import org.jetbrains.dukat.idlDeclarations.IDLExtendedAttributeDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLFileDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLTopLevelDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLTypedefDeclaration

internal class ModuleVisitor(private val fileName: String) : WebIDLBaseVisitor<IDLFileDeclaration>() {

    private var extendedAttributes: MutableList<IDLExtendedAttributeDeclaration> = mutableListOf()
    private val declarations: MutableList<IDLTopLevelDeclaration> = mutableListOf()

    override fun defaultResult() = IDLFileDeclaration(fileName, declarations)

    override fun visitDefinition(ctx: WebIDLParser.DefinitionContext?) : IDLFileDeclaration {
        declarations.add(DefinitionVisitor(extendedAttributes).visitDefinition(ctx))
        extendedAttributes = mutableListOf()
        return defaultResult()
    }

    override fun visitExtendedAttribute(ctx: WebIDLParser.ExtendedAttributeContext?) : IDLFileDeclaration {
        extendedAttributes.add(ExtendedAttributeVisitor().visit(ctx))
        return defaultResult()
    }

    override fun visitTypedef(ctx: WebIDLParser.TypedefContext) : IDLFileDeclaration {
        declarations.add(DefinitionVisitor(extendedAttributes).visit(ctx))
        return defaultResult()
    }

}
