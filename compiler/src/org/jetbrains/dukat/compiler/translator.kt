package org.jetbrains.dukat.compiler

import com.eclipsesource.v8.V8Object
import com.eclipsesource.v8.utils.V8ObjectUtils
import org.jetbrains.dukat.ast.AstFactory
import org.jetbrains.dukat.ast.j2v8.AstJ2V8Factory
import org.jetbrains.dukat.ast.j2v8.AstV8Factory
import org.jetbrains.dukat.ast.model.DocumentRoot
import org.jetbrains.dukat.ast.toAst
import org.jetbrains.dukat.interop.InteropEngine
import org.jetbrains.dukat.j2v8.interop.InteropV8
import org.jetbrains.dukat.j2v8.interop.InteropV8Signature
import org.jetbrains.dukat.nashorn.interop.InteropNashorn


fun createNashornInterop(resolver: ContentResolver): InteropNashorn {
    val engine = InteropNashorn()

    engine.eval("var global = this; var Set = Java.type('org.jetbrains.dukat.nashorn.Set');")

    engine.eval(resolver("tsserverlibrary.js"))
    engine.eval(resolver("dukat-ast-builder.js"))

    return engine
}


fun createV8Interop(resolver: ContentResolver): InteropV8 {
    val interopRuntime = InteropV8()

    interopRuntime.eval(resolver("tsserverlibrary.js"))
    interopRuntime.eval(resolver("dukat-ast-builder.js"))

    interopRuntime.proxy(System.out).method("println", InteropV8Signature.STRING)
    interopRuntime.eval("function AstFactoryV8() {}; function FileResolverV8() {}")

    interopRuntime
            .proxy(interopRuntime.executeScript("AstFactoryV8.prototype"), AstV8Factory(AstJ2V8Factory(interopRuntime.runtime)))
            .method("createExpression", InteropV8Signature.V8OBJECT, InteropV8Signature.STRING)
            .method("createTypeParam", InteropV8Signature.STRING)
            .method("createTypeDeclaration", InteropV8Signature.STRING, InteropV8Signature.V8ARRAY)
            .method("declareVariable", InteropV8Signature.STRING, InteropV8Signature.V8OBJECT)
            .method("createParameterDeclaration", InteropV8Signature.STRING, InteropV8Signature.V8OBJECT, InteropV8Signature.V8OBJECT)
            .method("createFunctionDeclaration", InteropV8Signature.STRING, InteropV8Signature.V8ARRAY, InteropV8Signature.V8OBJECT, InteropV8Signature.V8ARRAY)
            .method("createFunctionTypeDeclaration", InteropV8Signature.V8ARRAY, InteropV8Signature.V8OBJECT)
            .method("createDocumentRoot", InteropV8Signature.V8ARRAY)

    interopRuntime
            .proxy(interopRuntime.executeScript("FileResolverV8.prototype"), FileResolver())
            .method("resolve", InteropV8Signature.STRING)


    return interopRuntime
}

fun prodResourceResolver(fileName: String): String {
    val fileNameResolved = when (fileName) {
        "tsserverlibrary.js" -> "../ts/node_modules/typescript/lib/tsserverlibrary.js"
        "dukat-ast-builder.js" -> "../ts/build/ts/dukat-ast-builder.js"
        else -> fileName
    }

    return fileContent(fileNameResolved)
}

fun localResourceResolver(fileName: String): String {
    val fileNameResolved = when (fileName) {
        "tsserverlibrary.js" -> "ts/node_modules/typescript/lib/tsserverlibrary.js"
        "dukat-ast-builder.js" -> "ts/build/ts/dukat-ast-builder.js"
        else -> fileName
    }

    return fileContent(fileNameResolved)
}


fun createV8TranslatorFactory(interop: InteropEngine): (fileName: String) -> DocumentRoot {
    return { fileName ->
        val result = interop.callFunction<V8Object>("main", null, null, fileName)
        (V8ObjectUtils.toMap(result) as Map<String, Any?>).toAst()
    }
}


fun createNashornTranslatorFactory(engine: InteropEngine): (fileName: String) -> DocumentRoot {
    return { fileName -> engine.callFunction<DocumentRoot>("main", AstFactory(), FileResolver(), fileName) }
}


interface Translator {
    fun translateFile(fileName: String): DocumentRoot
    fun release()
}


class TranslatorV8(private val engine: InteropV8) : Translator {

    override fun translateFile(fileName: String): DocumentRoot {
        val result = engine.callFunction<V8Object>("main", null, null, fileName)
        return (V8ObjectUtils.toMap(result) as Map<String, Any?>).toAst()
    }

    override fun release() {
        engine.release()
    }
}

class TranslatorNashorn(private val engine: InteropNashorn) : Translator {
    override fun translateFile(fileName: String): DocumentRoot {
        return engine.callFunction<DocumentRoot>("main", AstFactory(), FileResolver(), fileName)
    }

    override fun release() {}
}


fun createV8Translator(resolver: ContentResolver = ::prodResourceResolver)
        = TranslatorV8(createV8Interop(resolver))

fun createNashornTranslator(resolver: ContentResolver = ::prodResourceResolver)
        = TranslatorNashorn(createNashornInterop(resolver))

fun createTranslator() = createNashornTranslator(::prodResourceResolver)

fun main() {
    val translator = createV8Translator(::localResourceResolver)
//    val translator = createNashornTranslator(::localResourceResolver)

    val astTree = translator.translateFile("./compiler/test/data/simplest_var.declarations.d.ts")

    println(compile(astTree))
    translator.release()
}