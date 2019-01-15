external interface Foo {
    fun bar()
    fun bar(a: Number)
    var baz: Any
}
external interface Boo : Foo {
    override fun bar()
    override fun bar(a: Number)
    fun bar(a: String)
    override var baz: Number
}
