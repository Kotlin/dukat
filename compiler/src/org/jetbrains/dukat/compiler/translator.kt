package org.jetbrains.dukat.compiler

import com.eclipsesource.v8.V8Object
import com.eclipsesource.v8.utils.V8ObjectUtils
import org.jetbrains.dukat.ast.j2v8.AstV8Factory
import org.jetbrains.dukat.compiler.translator.InputTranslator
import org.jetbrains.dukat.interop.InteropEngine
import org.jetbrains.dukat.j2v8.interop.InteropV8
import org.jetbrains.dukat.j2v8.interop.InteropV8Signature
import org.jetbrains.dukat.nashorn.interop.InteropNashorn
import org.jetbrains.dukat.tsmodel.DocumentRootDeclaration
import org.jetbrains.dukat.tsmodel.converters.toAst
import org.jetbrains.dukat.tsmodel.factory.AstFactory
import java.util.*


private fun readResource(name: String): String {
    println("READING RESOURCE ${name}")
    val resourceAsStream = object {}::class.java.classLoader.getResourceAsStream(name)
    return resourceAsStream.bufferedReader().readText()
}


private fun InteropEngine.loadAstBuilder() {
    eval(readResource("ts/tsserverlibrary.js"))
    eval(readResource("js/dukat-ast-builder.js"))
}

private fun createNashornInterop(): InteropNashorn {
    val engine = InteropNashorn()

    engine.eval("""
        var global = this;
        var Set = Java.type('org.jetbrains.dukat.nashorn.Set');
        var uid = function(){return Java.type('java.util.UUID').randomUUID().toString();}
    """.trimIndent())

    engine.loadAstBuilder()

    return engine
}

private fun createV8Interop(): InteropV8 {
    val interopRuntime = InteropV8()

    interopRuntime.loadAstBuilder()

    interopRuntime.proxy(System.out).method("println", InteropV8Signature.STRING)

    interopRuntime.proxy(object {
        fun uid() = UUID.randomUUID().toString()
    }).method("uid")

    interopRuntime.eval("function AstFactoryV8() {}; function FileResolverV8() {}")

    interopRuntime
            .proxy(interopRuntime.executeScript("AstFactoryV8.prototype"), AstV8Factory(AstFactory(), interopRuntime.runtime))
            .all()

    interopRuntime
            .proxy(interopRuntime.executeScript("FileResolverV8.prototype"), FileResolver())
            .method("resolve", InteropV8Signature.STRING)


    return interopRuntime
}

class TranslatorV8(private val engine: InteropV8) : InputTranslator {

    override fun translateFile(fileName: String): DocumentRootDeclaration {
        val result = engine.callFunction<V8Object>("main", null, null, fileName)
        return (V8ObjectUtils.toMap(result) as Map<String, Any?>).toAst()
    }

    override fun release() {
        engine.release()
    }
}

class TranslatorNashorn(private val engine: InteropNashorn) : InputTranslator {
    override fun translateFile(fileName: String): DocumentRootDeclaration {
        return engine.callFunction("main", AstFactory(), FileResolver(), fileName)
    }

    override fun release() {}
}


fun createV8Translator() = TranslatorV8(createV8Interop())
fun createNashornTranslator() = TranslatorNashorn(createNashornInterop())


fun main() {
    val translator = createV8Translator()

//    val astTree = translator.translateFile("./compiler/test/data/simplest_var.declarations.d.ts")
//
//    println(compile(astTree))
//    translator.release()
}