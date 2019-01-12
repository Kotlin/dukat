package exportTypes

external interface FooInterface {
    fun methodWithOutArgs()
    fun methodWithString(s: String): String
    fun methodWithManyArgs(n: Number, settings: Bar): Boolean
}
external open class FooClass {
    open fun methodWithOutArgs(): Unit = definedExternally
    open fun methodWithString(s: String): String = definedExternally
    open fun methodWithManyArgs(n: Number, settings: Bar): Boolean = definedExternally
}
