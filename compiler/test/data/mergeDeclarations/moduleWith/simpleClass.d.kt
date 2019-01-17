package simpleClass

external open class Foo {
    open fun bar(): Number = definedExternally
    companion object {
        fun baz(a: Any): Unit = definedExternally
        fun bar(): Number = definedExternally
    }
}
