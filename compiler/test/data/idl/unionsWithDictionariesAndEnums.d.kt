import kotlin.js.*

external abstract class A : D, UnionAOrBOrC

external interface B : D, UnionAOrBOrC

@kotlin.internal.InlineOnly
inline fun B(): B {
    val o = js("({})")
    return o
}

external abstract class E {
    open var x: D
    open var y: UnionAOrBOrC
}

external interface D

external interface UnionAOrBOrC

/* please, don't implement this interface! */
@Suppress("NESTED_CLASS_IN_EXTERNAL_INTERFACE")
external interface C : D, UnionAOrBOrC {
    companion object
}

inline val C.Companion.XX: C get() = "xx".asDynamic().unsafeCast<C>()