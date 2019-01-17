package classWithStaticMembers

external open class Foo {
    open fun bar(): Number = definedExternally
    companion object {
        var variable: String = definedExternally
        fun bar(): Unit = definedExternally
        fun baz(a: Any): Unit = definedExternally
    }
}
