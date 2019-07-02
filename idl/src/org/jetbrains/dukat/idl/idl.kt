package org.jetbrains.dukat.idl

import org.antlr.v4.runtime.CharStream
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.ParserRuleContext
import org.antlr.v4.runtime.tree.ParseTree
import org.antlr.v4.runtime.tree.TerminalNode
import org.antlr.webidl.WebIDLBaseVisitor
import org.antlr.webidl.WebIDLLexer
import org.antlr.webidl.WebIDLParser
import org.antlr.webidl.WebIDLParser.Interface_Context
import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.astModel.*
import org.jetbrains.dukat.translator.ROOT_PACKAGENAME
import java.io.File

interface Definition {
    fun toModel(): TopLevelModel
}
data class InterfaceDefinition(val name: String) : Definition {

    override fun toModel() : InterfaceModel {
        return InterfaceModel(
                name = IdentifierEntity(name),
                members = mutableListOf(),
                companionObject = CompanionObjectModel(
                        name = "",
                        members = mutableListOf(),
                        parentEntities = emptyList()
                ),
                typeParameters = mutableListOf(),
                parentEntities = mutableListOf(),
                annotations = mutableListOf(),
                external = false
        )
    }

}

class DefinitionVisitor : WebIDLBaseVisitor<Definition>() {

    override fun visitInterface_(ctx: Interface_Context): Definition {
        val name = getName(ctx)
        visitChildren(ctx)
        return InterfaceDefinition(name)
    }
}

class ModuleVisitor(val declarations: MutableList<Definition>) : WebIDLBaseVisitor<Unit>() {

    override fun visitDefinition(ctx: WebIDLParser.DefinitionContext?) {
        declarations.add(DefinitionVisitor().visitDefinition(ctx))
    }

}

private fun List<ParseTree>?.filterIdentifiers(): List<ParseTree> = this?.filter { it is TerminalNode && it.symbol.type == WebIDLLexer.IDENTIFIER_WEBIDL }
        ?: emptyList()

private fun getName(ctx: ParserRuleContext) = ctx.children.filterIdentifiers().first().text

fun parse(reader: CharStream): SourceSetModel {
    val lexer = WebIDLLexer(reader)
    val parser = WebIDLParser(CommonTokenStream(lexer))
    val idl = parser.webIDL()

    val declarations = ArrayList<Definition>()
    ModuleVisitor(declarations).visit(idl)

    val modelDeclarations = declarations.map { d -> d.toModel() }

    val module = ModuleModel(
            name = ROOT_PACKAGENAME,
            shortName = ROOT_PACKAGENAME,
            declarations = modelDeclarations,
            annotations = mutableListOf(),
            sumbodules = mutableListOf(),
            imports = mutableListOf()
    )

    val source = SourceFileModel(
            name = null,
            fileName = File(reader.sourceName).normalize().absolutePath,
            root = module,
            referencedFiles = listOf()
    )

    return SourceSetModel(listOf(source))
}
