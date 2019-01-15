package inObjectType

external interface `T$0` {
    var foo: Any /* this */
    fun bar(): Any /* this */
}
external interface `T$1` {
    var baz: Any /* this */
    fun boo(): Any /* this */
}
external fun foo(p: `T$0`): `T$1` = definedExternally
