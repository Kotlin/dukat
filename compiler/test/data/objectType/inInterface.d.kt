package inInterface

external interface `T$0` {
    fun bar(a: Any): Number
    var baz: Any? get() = definedExternally; set(value) = definedExternally
    var boo: Any? get() = definedExternally; set(value) = definedExternally
    var show: (overrideChecks: Boolean) -> Unit
}
external interface `T$1` {
    var value: Any? get() = definedExternally; set(value) = definedExternally
    var done: Boolean
}
external interface `T$2` {
    fun bar(a: Any): Number
    fun baz(a: Any, b: Any, c: String): Boolean
}
external interface Foo {
    fun withObjectTypeParam(opt: `T$0`)
    fun returnsObjectType(): `T$1`
    var foo: `T$2`
}
