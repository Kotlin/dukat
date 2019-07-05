package org.jetbrains.dukat.idlParser.visitors

import org.antlr.webidl.WebIDLBaseVisitor
import org.antlr.webidl.WebIDLParser
import org.jetbrains.dukat.idlDeclarations.IDLDeclaration

internal class ModuleVisitor(val declarations: MutableList<IDLDeclaration>) : WebIDLBaseVisitor<Unit>() {

    override fun visitDefinition(ctx: WebIDLParser.DefinitionContext?) {
        declarations.add(DefinitionVisitor().visitDefinition(ctx))
    }

}
