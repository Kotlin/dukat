import kotlin.js.*
import org.khronos.webgl.*

public external abstract class A {
    open var x: Int
    open var y: Int
    open var z: A<Int>?
    open var intArray: Array<Int>
    fun f(x: Int): Int

    companion object {
        val yy: Int
    }
}

public external abstract class B : A {
    open var xx: A<Int>

    companion object {
        val yy: Int
    }
}