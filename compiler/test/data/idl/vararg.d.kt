
import kotlin.js.*

external abstract class A {
    fun f(vararg arr: Int)
    fun g(length: Int, vararg arr: Int)
}