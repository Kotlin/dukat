import kotlin.js.*
import org.khronos.webgl.*

public external abstract class A {
    open var intVar: Int
    fun f1(): Double
    fun f1(x: Int): Double
    fun f2(other: A): Double
}

public external abstract class B : A {
    fun f2(): Double
    fun f2(other: B): Double
    fun f2(other: D): Double
}

public external abstract class C : A {
    fun f2(other: C): Double
}

public external abstract class D