package withNullOrUndefined

external var foo: String? = definedExternally
external var bar: String? = definedExternally
external fun bar(a: String?): Foo? = definedExternally
external fun baz(a: Foo?, b: Number? = definedExternally /* null */): Any? = definedExternally
external interface `T$0` {
    @nativeGetter
    operator fun get(key: String?): String?
    @nativeSetter
    operator fun set(key: String?, value: String?)
}
external open class Foo(a: String?) {
    open fun someMethod(): dynamic /* String | Number | Nothing? */ = definedExternally
    open var foo: Foo? = definedExternally
    open var optionalFoo: String? = definedExternally
    open var optionalFoo2: String? = definedExternally
    open var optionalFoo3: String? = definedExternally
    open var refs: `T$0` = definedExternally
}
external interface IBar {
    fun someMethod(): dynamic /* String | Number | Nothing? */
    var foo: Foo?
    var optionalFoo: String? get() = definedExternally; set(value) = definedExternally
    var optionalFoo2: String? get() = definedExternally; set(value) = definedExternally
    var optionalFoo3: String? get() = definedExternally; set(value) = definedExternally
}
