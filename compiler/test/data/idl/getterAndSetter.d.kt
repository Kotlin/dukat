
import kotlin.js.*

external abstract class A

@kotlin.internal.InlineOnly
inline operator fun A.get(x: Double): Int = asDynamic()[x]

@kotlin.internal.InlineOnly
inline operator fun A.get2(x: Float): Byte = asDynamic()[x]

@kotlin.internal.InlineOnly
inline operator fun A.set(index: Int, value: Float) { asDynamic()[index] = value }

@kotlin.internal.InlineOnly
inline operator fun A.set2(index: Byte, value: Double) { asDynamic()[index] = value }

