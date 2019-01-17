package simple

external var foo: dynamic /* String | Number */ = definedExternally
external fun bar(a: String): Unit = definedExternally
external fun bar(a: Number): Unit = definedExternally
external fun bar(a: Foo): Unit = definedExternally
external fun baz(a: String, b: Number): Unit = definedExternally
external fun baz(a: String, b: Foo): Unit = definedExternally
external fun baz(a: Number, b: Number): Unit = definedExternally
external fun baz(a: Number, b: Foo): Unit = definedExternally
external fun baz(a: Foo, b: Number): Unit = definedExternally
external fun baz(a: Foo, b: Foo): Unit = definedExternally
external interface `T$0` {
    @nativeGetter
    operator fun get(key: String): dynamic /* String | Number */
    @nativeSetter
    operator fun set(key: String, value: String)
    @nativeSetter
    operator fun set(key: String, value: Number)
}
external open class Foo {
    constructor(a: String)
    constructor(a: Number)
    open fun someMethod(): dynamic /* String | Number */ = definedExternally
    open var foo: dynamic /* String | Number */ = definedExternally
    open var optionalFoo: dynamic /* String | Number */ = definedExternally
    open var refs: `T$0` = definedExternally
}
