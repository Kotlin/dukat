
import kotlin.js.*

external abstract class A {
    var x: Int
    var y: Int
    var z: A<Int>?
    var intArray: Array<Int>
    fun f(x: Int): Int
    companion object {
        val y: Int
    }
}
external abstract class B : A {
    var x: A<Int>
    companion object {
        val y: Int
    }
}