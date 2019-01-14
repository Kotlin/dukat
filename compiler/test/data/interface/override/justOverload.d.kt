package justOverload

external interface Foo {
    fun bar(a: Number)
}
external interface Boo : Foo {
    fun bar(a: String)
}
