package org.jetbrains.dukat.ast

import java.io.FileReader
import javax.script.Invocable
import javax.script.ScriptEngineManager

actual fun translator(astTree: AstTree, fileResolver: FileResolver) {
    val engineManager = ScriptEngineManager()
    var engine = engineManager.getEngineByName("nashorn")
    engine.eval("var global = this;")
    engine.eval(FileReader("./ts/build/ts/jvm/Set.js"));
    engine.eval(FileReader("./ts/node_modules/requirejs/require.js"));
    engine.eval(FileReader("./ts/node_modules/typescript/lib/tsserverlibrary.js"))
    engine.eval(FileReader("./ts/build/ts/converter.js"));

    val invocable = engine as Invocable
    invocable.invokeFunction("main", astTree, AstFactory(), fileResolver)
}



fun main() {
    translator(AstTree(DocumentRoot()), FileResolver())
}