package useOneTraitForSameObjectTypes

external interface `T$0` {
    fun bar(a: Any): Number
    var baz: Any
    var boo: String
    var show: (overrideChecks: Boolean) -> Unit
    @nativeGetter
    operator fun get(s: String): Any?
    @nativeSetter
    operator fun set(s: String, value: Any)
}
external fun foo(): `T$0` = definedExternally
external fun bar(): `T$0` = definedExternally
external interface `T$1` {
    fun bar(a: Any): Number
    var baz: Any
    var boo: String
    var show: (flag: Boolean) -> Unit
    @nativeGetter
    operator fun get(s: String): Number?
    @nativeSetter
    operator fun set(s: String, value: Number)
}
external fun baz(): `T$1` = definedExternally
external interface Interface {
    var bar: `T$0`
    var baz: `T$1`
}
