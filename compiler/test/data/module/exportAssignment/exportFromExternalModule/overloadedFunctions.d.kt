package overloadedFunctions

external interface BrowserifyObject : NodeJS.EventEmitter {
    fun add(file: String): BrowserifyObject
    fun require(file: String): BrowserifyObject
}
external interface Options {
    var entries: Array<String>? get() = definedExternally; set(value) = definedExternally
    var noParse: Array<String>? get() = definedExternally; set(value) = definedExternally
}

// ------------------------------------------------------------------------------------------
package overloadedFunctions.browserify

@JsModule("browserify")
external fun browserify(): BrowserifyObject = definedExternally
@JsModule("browserify")
external fun browserify(files: Array<String>): BrowserifyObject = definedExternally
@JsModule("browserify")
external fun browserify(opts: Options): BrowserifyObject = definedExternally
