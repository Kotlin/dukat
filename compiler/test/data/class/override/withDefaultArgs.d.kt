package withDefaultArgs

external open class Foo {
    open fun bar(a: Number = definedExternally /* 1 */): Unit = definedExternally
    open fun baz(a: Any? = definedExternally /* null */): Unit = definedExternally
}
external open class Boo : Foo {
    override fun bar(a: Number): Unit = definedExternally
    override fun baz(a: Any?): Unit = definedExternally
}
