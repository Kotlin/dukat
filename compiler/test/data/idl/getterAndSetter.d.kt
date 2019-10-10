import kotlin.js.*

public external abstract class A {
    fun get2(x: Float): Byte
    fun set2(index: Byte, value: Double)
}

@kotlin.internal.InlineOnly
public inline operator fun A.get(x: Double): Int = asDynamic()[x]

@kotlin.internal.InlineOnly
public inline operator fun A.get(x: Float): Byte = asDynamic()[x]

@kotlin.internal.InlineOnly
public inline operator fun A.set(index: Int, value: Float) { asDynamic()[index] = value }

@kotlin.internal.InlineOnly
public inline operator fun A.set(index: Byte, value: Double) { asDynamic()[index] = value }

