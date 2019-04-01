package org.jetbrains.dukat.cli

import org.jetbrains.dukat.ast.model.nodes.process
import org.jetbrains.dukat.ast.model.nodes.translate
import org.jetbrains.dukat.astModel.flattenDeclarations
import org.jetbrains.dukat.compiler.createV8Translator
import translateModule
import java.io.File


private fun unescape(name: String): String {
    return name.replace("(?:^`)|(?:`$)".toRegex(), "")
}


private fun compile(filename: String) {
    println("Converting $filename")
    val translator = createV8Translator()

    val sourceSetModel = translator.translate(filename)

    println("Save declarations:")

    sourceSetModel.sources.forEach { source ->
        println("--- ${source.fileName} -----------")
        val modules = source.root.flattenDeclarations()

        modules.forEach { module ->

            val targetName = "${module.packageName.process(::unescape).translate()}.kt"

            println("generating ${targetName}")
            File(targetName).writeText(translateModule(module).joinToString("\n"))
        }
    }
}

fun main(vararg args: String) {
    val tsDeclaration = args.get(0)
    compile(tsDeclaration)
}