package methods

external open class Foo {
    open fun methodWithOutArgs(): Unit = definedExternally
    open fun methodWithString(s: String): String = definedExternally
    open fun methodWithManyArgs(n: Number, settings: Bar): Boolean = definedExternally
}
