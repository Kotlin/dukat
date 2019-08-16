
import kotlin.js.*

external abstract class A {
    var intVar: Int
    fun f1(): Double
    fun f1(x: Int): Double
    fun f2(other: A): Double
}
external abstract class B : A {
    fun f2(): Double
    override fun f2(other: B): Double
    fun f2(other: D): Double
}
external abstract class C : A {
    override fun f2(other: C): Double
}
external abstract class D