import kotlin.js.*

public external abstract class UnresolvedUnions {
    open var x: dynamic
    open var y: dynamic
    fun f(x: dynamic): dynamic
}

public external abstract class A : ABC

public external abstract class B : ABC

public external abstract class C : ABC, UnionCOrD

public external abstract class D : ABCD, UnionCOrD

public external abstract class ResolvedUnions {
    open var x: ABC?
    open var y: Array<ABCD>
    open var z: UnionCOrD?
    open var w: dynamic
    fun f(x: ABC?): ABC
}

public external interface ABC : ABCD

public external interface ABCD

public external interface UnionCOrD
