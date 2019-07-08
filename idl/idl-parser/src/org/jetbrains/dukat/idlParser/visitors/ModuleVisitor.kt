package org.jetbrains.dukat.idlParser.visitors

import org.antlr.webidl.WebIDLBaseVisitor
import org.antlr.webidl.WebIDLParser
import org.jetbrains.dukat.idlDeclarations.IDLTopLevelDeclaration

internal class ModuleVisitor(val declarations: MutableList<IDLTopLevelDeclaration>) : WebIDLBaseVisitor<Unit>() {

    override fun visitDefinition(ctx: WebIDLParser.DefinitionContext?) {
        declarations.add(DefinitionVisitor().visitDefinition(ctx))
    }

}
