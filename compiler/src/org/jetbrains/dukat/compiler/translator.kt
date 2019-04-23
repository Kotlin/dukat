package org.jetbrains.dukat.compiler

import com.eclipsesource.v8.V8Object
import com.eclipsesource.v8.utils.V8ObjectUtils
import mu.KLogger
import mu.KotlinLogging
import org.jetbrains.dukat.ast.j2v8.AstV8Factory
import org.jetbrains.dukat.compiler.translator.TypescriptInputTranslator
import org.jetbrains.dukat.interop.InteropEngine
import org.jetbrains.dukat.interop.graal.InteropGraal
import org.jetbrains.dukat.j2v8.interop.InteropV8
import org.jetbrains.dukat.j2v8.interop.InteropV8Signature
import org.jetbrains.dukat.nashorn.DocumentCache
import org.jetbrains.dukat.nashorn.interop.InteropNashorn
import org.jetbrains.dukat.tsinterop.ExportContent
import org.jetbrains.dukat.tsinterop.ExportContentNonGeneric
import org.jetbrains.dukat.tsmodel.SourceSetDeclaration
import org.jetbrains.dukat.tsmodel.converters.toAst
import org.jetbrains.dukat.tsmodel.factory.AstFactory
import org.slf4j.Logger
import java.util.*


private fun InteropEngine.loadAstBuilder() {
    val fileResolver = FileResolver()
    eval(fileResolver.readResource("ts/tsserverlibrary.js"))
    eval(fileResolver.readResource("js/dukat-ast-builder.js"))
}

private fun createNashornInterop(): InteropNashorn {
    val engine = InteropNashorn()

    engine.put("AstFactory", AstFactory::class.java)
    engine.put("FileResolver", FileResolver::class.java)
    engine.put("ExportContent", ExportContentNonGeneric::class.java)
    engine.put("KotlinLogging", KotlinLogging::class.java)

    engine.eval("""
        var global = this;
        var DocumentCache = Java.type('org.jetbrains.dukat.nashorn.DocumentCache');
        var Set = Java.type('org.jetbrains.dukat.nashorn.Set');

        function createExportContent() {return new ExportContent.static(); }
        function createAstFactory() { return new AstFactory.static(); }
        function createFileResolver() { return new FileResolver.static(); }

        function createLogger(name) {
            return KotlinLogging.static.INSTANCE.logger(name);
        }

        var uid = function(){return Java.type('java.util.UUID').randomUUID().toString();}
    """.trimIndent())

    engine.loadAstBuilder()

    return engine
}

private fun createGraalInterop(): InteropGraal {
    val engine = InteropGraal()

    engine.put("AstFactory", AstFactory::class.java)
    engine.put("FileResolver", FileResolver::class.java)
    engine.put("ExportContent", ExportContentNonGeneric::class.java)
    engine.put("KotlinLogging", KotlinLogging::class.java)

    engine.eval("""
        var global = this;

        function createExportContent() {return new ExportContent.static(); }
        function createAstFactory() { return new AstFactory.static(); }
        function createFileResolver() { return new FileResolver.static(); }

        function createLogger(name) {
            return  KotlinLogging.static.INSTANCE.logger(name)
        }

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
        fun createExportContent(): V8Object {
            val proxy = V8Object(interopRuntime.runtime)
            interopRuntime.proxy(proxy, ExportContent<V8Object>() { it.twin() }).all()
            return proxy
        }

        fun createAstFactory(): V8Object {
            val proxy = V8Object(interopRuntime.runtime)
            interopRuntime.proxy(proxy, AstV8Factory(AstFactory(), interopRuntime.runtime)).all()
            return proxy
        }

        fun createFileResolver(): V8Object {
            val proxy = V8Object(interopRuntime.runtime)
            interopRuntime.proxy(proxy, FileResolver()).all()
            return proxy
        }

        fun createLogger(name: String) : V8Object {
            val proxy = V8Object(interopRuntime.runtime)
            interopRuntime.proxy(proxy, KotlinLogging.logger(name))
                    .method("debug", InteropV8Signature.STRING)
                    .method("trace", InteropV8Signature.STRING)
                    .method("info", InteropV8Signature.STRING)
                    .method("warn", InteropV8Signature.STRING)

            return proxy
        }
    }).all()

    return interopRuntime
}

class TranslatorV8(private val engine: InteropV8) : TypescriptInputTranslator {

    override fun translateFile(fileName: String): SourceSetDeclaration {
        val result = engine.callFunction<V8Object>("main", fileName)
        return (V8ObjectUtils.toMap(result) as Map<String, Any?>).toAst()
    }

    override fun release() {
        engine.release()
    }
}

class TranslatorNashorn(
        private val engine: InteropNashorn,
        private val documentCache: DocumentCache = DocumentCache()
) : TypescriptInputTranslator {
    override fun translateFile(fileName: String): SourceSetDeclaration {
        return engine.callFunction("main", fileName, documentCache)
    }

    override fun release() {}
}

class TranslatorGraal(
        private val engine: InteropGraal,
        private val documentCache: DocumentCache = DocumentCache()
) : TypescriptInputTranslator {
    override fun translateFile(fileName: String): SourceSetDeclaration {
        return engine.callFunction("main", fileName, documentCache)
    }

    override fun release() {}
}

fun createV8Translator() = TranslatorV8(createV8Interop())
fun createNashornTranslator() = TranslatorNashorn(createNashornInterop())
fun createGraalTranslator() = TranslatorGraal(createGraalInterop())