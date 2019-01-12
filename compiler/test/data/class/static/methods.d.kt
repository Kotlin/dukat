package methods

external open class Foo {
    companion object {
        fun methodWithOutArgs(): Unit = definedExternally
        fun methodWithString(s: String): String = definedExternally
        fun methodWithManyArgs(n: Number, settings: Bar): Boolean = definedExternally
    }
}
