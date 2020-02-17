import kotlin.js.*
import org.khronos.webgl.*

/* please, don't implement this interface! */
@Suppress("NESTED_CLASS_IN_EXTERNAL_INTERFACE")
public external interface A {
    companion object
}

public inline val A.Companion.AAA: A get() = "aaa".asDynamic().unsafeCast<A>()

public inline val A.Companion.BBB: A get() = "BBB".asDynamic().unsafeCast<A>()

public inline val A.Companion.CCC_DDD: A get() = "ccc-ddd".asDynamic().unsafeCast<A>()

public inline val A.Companion.EMPTY: A get() = "".asDynamic().unsafeCast<A>()

public inline val A.Companion.E55: A get() = "e55".asDynamic().unsafeCast<A>()

public inline val A.Companion._5FF: A get() = "5ff".asDynamic().unsafeCast<A>()

public inline val A.Companion._55G: A get() = "55g".asDynamic().unsafeCast<A>()

public inline val A.Companion.A_B: A get() = "a/b".asDynamic().unsafeCast<A>()

public inline val A.Companion.A_B_C: A get() = "a/b+c".asDynamic().unsafeCast<A>()