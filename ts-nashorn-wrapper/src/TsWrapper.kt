import java.io.FileReader
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.Invocable



fun main() {
    val engineManager = ScriptEngineManager()
    var engine = engineManager.getEngineByName("nashorn")
    engine.eval(FileReader("./ts/build/ts/converter.js"));

    val invocable = engine as Invocable;
    val someList = mutableListOf<Int>()
    invocable.invokeFunction("ping", someList)
    invocable.invokeFunction("ping", someList)
    invocable.invokeFunction("ping", someList)
    invocable.invokeFunction("ping", someList)

    println(someList)
}