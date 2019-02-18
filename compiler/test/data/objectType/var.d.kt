package `var`

external interface `T$0` {
    var bar: Number
}
external object foo {
    fun bar(a: Any): Number = definedExternally
    var baz: Any = definedExternally
    var boo: String = definedExternally
    var show: (overrideChecks: Boolean) -> Unit = definedExternally
    @nativeInvoke
    operator fun invoke(foo: `T$0`): Any = definedExternally
    @nativeGetter
    operator fun get(s: String): Any? = definedExternally
    @nativeSetter
    operator fun set(s: String, value: Any): Unit = definedExternally
}