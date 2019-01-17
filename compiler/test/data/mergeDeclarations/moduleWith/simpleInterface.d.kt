package simpleInterface

external interface Foo {
    fun bar(): Number
    companion object {
        fun baz(a: Any): Unit = definedExternally
        fun bar(): Number = definedExternally
    }
}
