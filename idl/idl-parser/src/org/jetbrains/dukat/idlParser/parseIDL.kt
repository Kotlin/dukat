package org.jetbrains.dukat.idlParser

import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.webidl.WebIDLLexer
import org.antlr.webidl.WebIDLParser
import org.jetbrains.dukat.idlDeclarations.IDLFileDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLTopLevelDeclaration
import org.jetbrains.dukat.idlParser.visitors.ModuleVisitor

fun parseIDL(fileName: String): IDLFileDeclaration {
    val reader = CharStreams.fromFileName(fileName, Charsets.UTF_8)

    val lexer = WebIDLLexer(reader)
    val parser = WebIDLParser(CommonTokenStream(lexer))
    val idl = parser.webIDL()

    val declarations = ArrayList<IDLTopLevelDeclaration>()
    ModuleVisitor(declarations).visit(idl)

    return IDLFileDeclaration(fileName, declarations)
}
