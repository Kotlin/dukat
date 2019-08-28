import kotlin.js.*

/* please, don't implement this interface! */
@Suppress("NESTED_CLASS_IN_EXTERNAL_INTERFACE")
external interface A {
    companion object
}

inline val A.Companion.AAA: A get() = "aaa".asDynamic().unsafeCast<A>()

inline val A.Companion.BBB: A get() = "BBB".asDynamic().unsafeCast<A>()

inline val A.Companion.CCC_DDD: A get() = "ccc-ddd".asDynamic().unsafeCast<A>()

inline val A.Companion.EMPTY: A get() = "".asDynamic().unsafeCast<A>()

inline val A.Companion.E55: A get() = "e55".asDynamic().unsafeCast<A>()

inline val A.Companion._5FF: A get() = "5ff".asDynamic().unsafeCast<A>()

inline val A.Companion._55G: A get() = "55g".asDynamic().unsafeCast<A>()

inline val A.Companion.A_B: A get() = "a/b".asDynamic().unsafeCast<A>()

inline val A.Companion.A_B_C: A get() = "a/b+c".asDynamic().unsafeCast<A>()