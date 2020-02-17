import kotlin.js.*
import org.khronos.webgl.*

public external abstract class A {
    fun get2(x: Float): Byte
    fun set2(index: Byte, value: Double)
}

@Suppress("INVISIBLE_REFERENCE", "INVISIBLE_MEMBER")
@kotlin.internal.InlineOnly
public inline operator fun A.get(x: Double): Int = asDynamic()[x]

@Suppress("INVISIBLE_REFERENCE", "INVISIBLE_MEMBER")
@kotlin.internal.InlineOnly
public inline operator fun A.get(x: Float): Byte = asDynamic()[x]

@Suppress("INVISIBLE_REFERENCE", "INVISIBLE_MEMBER")
@kotlin.internal.InlineOnly
public inline operator fun A.set(index: Int, value: Float) { asDynamic()[index] = value }

@Suppress("INVISIBLE_REFERENCE", "INVISIBLE_MEMBER")
@kotlin.internal.InlineOnly
public inline operator fun A.set(index: Byte, value: Double) { asDynamic()[index] = value }

