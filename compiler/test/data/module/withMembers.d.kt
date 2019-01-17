@file:JsQualifier("Foo")
package withMembers.Foo

external interface A {
    fun baz()
}
external open class B {
    open fun boo(): Unit = definedExternally
}
external var c: Number = definedExternally
external fun d(a: Boolean, b: Any, c: SomeType): Unit = definedExternally
