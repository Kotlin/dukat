package org.jetbrains.dukat.idlParser

import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.webidl.WebIDLLexer
import org.antlr.webidl.WebIDLParser
import org.jetbrains.dukat.idlDeclarations.IDLSourceSetDeclaration
import org.jetbrains.dukat.idlParser.visitors.ModuleVisitor
import org.jetbrains.dukat.translatorString.IDL_DECLARATION_EXTENSION
import org.jetbrains.dukat.translatorString.WEBIDL_DECLARATION_EXTENSION
import java.io.File

fun parseIDL(mainFileName: String): IDLSourceSetDeclaration {

    return IDLSourceSetDeclaration(
            files = (listOf(mainFileName) + getReferenced(mainFileName)).map {fileName ->
                val reader = CharStreams.fromFileName(fileName, Charsets.UTF_8)
                val lexer = WebIDLLexer(reader)
                val parser = WebIDLParser(CommonTokenStream(lexer))
                val idl = parser.webIDL()

                ModuleVisitor(fileName).visit(idl)
            }
    )
}

fun getReferenced(fileName: String): List<String> {
    val directory = File(fileName).parentFile
    return directory.listFiles()?.filter {
        it.isFile &&
                it.extension in listOf(WEBIDL_DECLARATION_EXTENSION, IDL_DECLARATION_EXTENSION) &&
                it.absolutePath != File(fileName).absolutePath
    }?.map { it.absolutePath }.orEmpty()
}