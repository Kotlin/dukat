package withInterface

@JsModule("lazy.js")
external val Lazy: LazyJS.LazyStatic = definedExternally

// ------------------------------------------------------------------------------------------
@file:JsQualifier("LazyJS")
package withInterface.LazyJS

external interface LazyStatic {
    fun foo(a: Number)
}
external var a: Any = definedExternally
