
import kotlin.js.*

external open class A(a: Int) {
    open var x: Int
    open var y: Int
    fun f()
    fun g()
    companion object {
        val z: Int
    }
}
external interface B {
    var a: Boolean? /* = true */ get() = definedExternally; set(value) = definedExternally
    var b: Boolean? /* = false */ get() = definedExternally; set(value) = definedExternally
}
@kotlin.internal.InlineOnly
inline fun B(a: Boolean? = true, b: Boolean? = false): B { val o = js("({})"); o["a"] = a; o["b"] = b; return o }