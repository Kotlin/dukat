@file:JsQualifier("React")
package classWithInterface.React

external open class Component<P, S> : Bar, Foo, ComponentLifecycle<P, S> {
    open fun boo(p: P, s: S): Unit = definedExternally
    var foo: String
    fun bar(): Number
    fun baz(a: Any)
}
