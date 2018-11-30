package org.jetbrains.dukat.ast

import java.io.FileReader
import javax.script.Invocable
import javax.script.ScriptEngine
import javax.script.ScriptEngineManager



fun getEngine(): ScriptEngine {
    val engineManager = ScriptEngineManager()
    var engine = engineManager.getEngineByName("nashorn")

    engine.eval("var global = this;")
    engine.eval(FileReader("build/resources/jvm/Set.js"));
    engine.eval(FileReader("build/resources/require.js"));
    engine.eval(FileReader("build/resources/typescript/lib/tsserverlibrary.js"))
    engine.eval(FileReader("build/resources/converter.js"));

    return engine
}

actual fun createTranslator(): (fileName: String) -> AstTree {
    val engine = getEngine()
    val invocable = engine as Invocable
    return {fileName -> invocable.invokeFunction("main", AstFactory(), FileResolver(), fileName) as AstTree}
}


fun main() {
    val astTree = createTranslator()("./ast/common/test/data/simplest_var.declarations.d.ts")
    println(compile(astTree))
}