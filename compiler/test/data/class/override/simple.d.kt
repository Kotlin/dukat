package simple

external open class Foo {
    open fun bar(): Unit = definedExternally
    open fun bar(a: Number): Unit = definedExternally
    open var baz: Any = definedExternally
}
external open class Boo : Foo {
    override fun bar(): Unit = definedExternally
    override fun bar(a: Number): Unit = definedExternally
    open fun bar(a: String): Unit = definedExternally
    override var baz: Number = definedExternally
}
