@file:JsQualifier("Foo")
package nestedModulesWithSameName.Foo

external interface A {
    fun baz()
}
external fun d(a: A, b: Any, c: Foo.Foo.B): Unit = definedExternally

// ------------------------------------------------------------------------------------------
@file:JsQualifier("Foo.Foo")
package nestedModulesWithSameName.Foo.Foo

external open class B {
    open fun boo(): Foo.A = definedExternally
}
external var c: Number = definedExternally
