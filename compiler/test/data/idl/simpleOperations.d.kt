import kotlin.js.*
import org.khronos.webgl.*

public external abstract class A {
    fun f1(): Double
    fun f2(x: Int, y: Int): Double
    fun f3(x: A): A
    fun f4()
}