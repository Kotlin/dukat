package tscEscaping

external var __foo: Any = definedExternally
external fun __express(filename: String, options: Any, callback: Function<*>): Any = definedExternally
external open class __A {
    open var __foo: Number = definedExternally
    open fun __express(filename: String, options: Any, callback: Function<*>): Any = definedExternally
}
external interface __B {
    var __foo: Number
    fun __express(filename: String, options: Any, callback: Function<*>): Any
}
external enum class __E {
    __A,
    __B
}
external fun <__T> foo(__a: __T, _b: __M.__N.__C): Unit = definedExternally

// ------------------------------------------------------------------------------------------
@file:JsModule("atpl")
package tscEscaping.atpl

external var __foo: Any = definedExternally
external fun __express(filename: String, options: Any, callback: Function<*>): Any = definedExternally

// ------------------------------------------------------------------------------------------
@file:JsQualifier("__M")
package tscEscaping.__M

external var __foo: Number = definedExternally
external fun __express(filename: String, options: Any, callback: Function<*>): Any = definedExternally

// ------------------------------------------------------------------------------------------
@file:JsQualifier("__M.__N")
package tscEscaping.__M.__N

external var __foo: Number = definedExternally
external fun __express(filename: String, options: Any, callback: Function<*>): Any = definedExternally
external open class __C
