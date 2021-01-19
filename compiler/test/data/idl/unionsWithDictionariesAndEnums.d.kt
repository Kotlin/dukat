import kotlin.js.*
import org.khronos.webgl.*

public external abstract class A : D, UnionAOrBOrC

public external interface B : D, UnionAOrBOrC

@Suppress("INVISIBLE_REFERENCE", "INVISIBLE_MEMBER")
@kotlin.internal.InlineOnly
public inline fun B(): B {
    val o = js("({})")
    return o
}

public external abstract class E {
    open var x: D
    open var y: UnionAOrBOrC
}

public external interface UnionAOrBOrC

public external interface D

/* please, don't implement this interface! */
@JsName("null")
@Suppress("NESTED_CLASS_IN_EXTERNAL_INTERFACE")
public external interface C : D, UnionAOrBOrC {
    companion object
}

public inline val C.Companion.XX: C get() = "xx".asDynamic().unsafeCast<C>()
