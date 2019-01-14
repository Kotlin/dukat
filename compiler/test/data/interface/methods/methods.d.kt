package methods

external interface Foo {
    fun methodWithOutArgs()
    fun methodWithString(s: String): String
    fun methodWithManyArgs(n: Number, settings: Bar): Boolean
}

// ------------------------------------------------------------------------------------------
@file:JsQualifier("foo")
package methods.foo

external interface Bar {
    fun methodWithString(s: String): String
}
