package org.jetbrains.dukat.idlParser

import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.webidl.WebIDLLexer
import org.antlr.webidl.WebIDLParser
import org.jetbrains.dukat.idlDeclarations.IDLSourceSetDeclaration
import org.jetbrains.dukat.idlParser.visitors.ModuleVisitor
import org.jetbrains.dukat.idlReferenceResolver.IdlReferencesResolver

fun parseIDL(mainFileName: String, referencesResolver: IdlReferencesResolver): IDLSourceSetDeclaration {

    return IDLSourceSetDeclaration(
            files = (listOf(mainFileName) + referencesResolver.resolveReferences(mainFileName)).map { fileName ->
                val reader = CharStreams.fromFileName(fileName, Charsets.UTF_8)
                val lexer = WebIDLLexer(reader)
                val parser = WebIDLParser(CommonTokenStream(lexer))
                val idl = parser.webIDL()

                ModuleVisitor(fileName).visit(idl).copy(
                        referencedFiles = referencesResolver.resolveReferences(fileName)
                )
            }
    )
}
