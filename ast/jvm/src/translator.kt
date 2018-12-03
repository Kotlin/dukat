package org.jetbrains.dukat.ast

import java.io.FileReader
import javax.script.Invocable
import javax.script.ScriptEngine
import javax.script.ScriptEngineManager



fun getEngine(resolver: ContentResolver): ScriptEngine {
    val engineManager = ScriptEngineManager()
    var engine = engineManager.getEngineByName("nashorn")

    engine.eval("var global = this; var Set = Java.type('org.jetbrains.dukat.nashorn.Set');")

    engine.eval(resolver("tsserverlibrary.js"))
    engine.eval(resolver("converter.js"))

    return engine
}

fun prodResourceResolver(fileName: String): String {
    val fileNameResolved = when(fileName) {
        "tsserverlibrary.js" -> "../ts/node_modules/typescript/lib/tsserverlibrary.js"
        "converter.js" ->  "../ts/build/ts/converter.js"
        else -> fileName
    }

    return fileContent(fileNameResolved)
}

fun localResourceResolver(fileName: String): String {
    val fileNameResolved = when(fileName) {
        "tsserverlibrary.js" -> "ts/node_modules/typescript/lib/tsserverlibrary.js"
        "converter.js" ->  "ts/build/ts/converter.js"
        else -> fileName
    }

    return fileContent(fileNameResolved)
}


fun createTranslatorFactory(resourceResolver: ContentResolver): (fileName: String) -> AstTree  {
    val engine = getEngine(resourceResolver)
    val invocable = engine as Invocable
    return {fileName -> invocable.invokeFunction("main", AstFactory(), FileResolver(), fileName) as AstTree}
}

actual fun createTranslator() = createTranslatorFactory(::prodResourceResolver)


fun main() {
    val astTree = createTranslatorFactory(::localResourceResolver)("./ast/common/test/data/simplest_var.declarations.d.ts")
    println(compile(astTree))
}