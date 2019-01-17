@file:JsModule("Boo")
package exportedByDefault.Boo

external fun foo(): String = definedExternally
external var bar: Number = definedExternally
external interface IBaz {
    fun doSomething()
}
external open class Klass {
    open fun method(s: Any): String = definedExternally
}
