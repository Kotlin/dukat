
import kotlin.js.*

external abstract class UnresolvedUnions {
    open var x: dynamic
    open var y: dynamic
    fun f(x: dynamic): dynamic
}
external abstract class A : ABC
external abstract class B : ABC
external abstract class C : ABC, UnionCOrD
external abstract class D : ABCD, UnionCOrD
external abstract class ResolvedUnions {
    open var x: ABC?
    open var y: Array<ABCD>
    open var z: UnionCOrD?
    open var w: dynamic
    fun f(x: ABC?): ABC
}
external interface ABC : ABCD
external interface ABCD
external interface UnionCOrD
