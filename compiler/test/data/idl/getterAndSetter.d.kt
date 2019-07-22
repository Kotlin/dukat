
external abstract class A
@kotlin.internal.InlineOnly inline operator fun A.get(x: Double): Int = asDynamic()[index]
@kotlin.internal.InlineOnly inline operator fun A.set(index: Int, value: Float) { asDynamic()[index] = value; }
