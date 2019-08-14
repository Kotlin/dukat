package org.jetbrains.dukat.idlParser.visitors

import org.antlr.webidl.WebIDLBaseVisitor
import org.antlr.webidl.WebIDLParser
import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astCommon.toNameEntity
import org.jetbrains.dukat.idlDeclarations.IDLExtendedAttributeDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLFileDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLTopLevelDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLTypedefDeclaration
import org.jetbrains.dukat.idlParser.filterIdentifiers

internal class ModuleVisitor(private val fileName: String) : WebIDLBaseVisitor<IDLFileDeclaration>() {

    private var extendedAttributes: MutableList<IDLExtendedAttributeDeclaration> = mutableListOf()
    private val declarations: MutableList<IDLTopLevelDeclaration> = mutableListOf()
    private var packageName: NameEntity? = null

    override fun defaultResult() = IDLFileDeclaration(fileName, declarations, listOf(), packageName)

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

    override fun visitPackageRest(ctx: WebIDLParser.PackageRestContext): IDLFileDeclaration {
        packageName = ctx.text.toNameEntity()
        return defaultResult()
    }

}
