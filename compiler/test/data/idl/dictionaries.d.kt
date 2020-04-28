import kotlin.js.*
import org.khronos.webgl.*

public external interface WebGLContextAttributes {
    var alpha: Boolean? /* = true */
    var depth: Boolean? /* = true */
    var stencil: Boolean? /* = false */
    var antialias: Boolean? /* = true */
    var premultipliedAlpha: Boolean? /* = true */
    var preserveDrawingBuffer: Boolean? /* = false */
    var preferLowPowerToHighPerformance: Boolean? /* = false */
    var failIfMajorPerformanceCaveat: Boolean? /* = false */
}

@Suppress("INVISIBLE_REFERENCE", "INVISIBLE_MEMBER")
@kotlin.internal.InlineOnly
public inline fun WebGLContextAttributes(alpha: Boolean? = true, depth: Boolean? = true, stencil: Boolean? = false, antialias: Boolean? = true, premultipliedAlpha: Boolean? = true, preserveDrawingBuffer: Boolean? = false, preferLowPowerToHighPerformance: Boolean? = false, failIfMajorPerformanceCaveat: Boolean? = false): WebGLContextAttributes {
    val o = js("({})")
    o["alpha"] = alpha
    o["depth"] = depth
    o["stencil"] = stencil
    o["antialias"] = antialias
    o["premultipliedAlpha"] = premultipliedAlpha
    o["preserveDrawingBuffer"] = preserveDrawingBuffer
    o["preferLowPowerToHighPerformance"] = preferLowPowerToHighPerformance
    o["failIfMajorPerformanceCaveat"] = failIfMajorPerformanceCaveat
    return o
}

public external interface PointerEventInit {
    var pointerId: Int? /* = 0 */
    var width: Double? /* = 1.0 */
    var height: Double? /* = 1.0 */
    var pressure: Float? /* = 0f */
    var tangentialPressure: Float? /* = 0f */
    var tiltX: Int? /* = 0 */
    var tiltY: Int? /* = 0 */
    var twist: Int? /* = 0 */
    var pointerType: String? /* = "" */
    var isPrimary: Boolean? /* = false */
}

@Suppress("INVISIBLE_REFERENCE", "INVISIBLE_MEMBER")
@kotlin.internal.InlineOnly
public inline fun PointerEventInit(pointerId: Int? = 0, width: Double? = 1.0, height: Double? = 1.0, pressure: Float? = 0f, tangentialPressure: Float? = 0f, tiltX: Int? = 0, tiltY: Int? = 0, twist: Int? = 0, pointerType: String? = "", isPrimary: Boolean? = false): PointerEventInit {
    val o = js("({})")
    o["pointerId"] = pointerId
    o["width"] = width
    o["height"] = height
    o["pressure"] = pressure
    o["tangentialPressure"] = tangentialPressure
    o["tiltX"] = tiltX
    o["tiltY"] = tiltY
    o["twist"] = twist
    o["pointerType"] = pointerType
    o["isPrimary"] = isPrimary
    return o
}

public external abstract class A {
    open var attributes: WebGLContextAttributes
}

public external interface UndefinedMemberDictionary {
    var str: String?
    var str2: String?
}

@Suppress("INVISIBLE_REFERENCE", "INVISIBLE_MEMBER")
@kotlin.internal.InlineOnly
public inline fun UndefinedMemberDictionary(str: String?, str2: String? = undefined): UndefinedMemberDictionary {
    val o = js("({})")
    o["str"] = str
    o["str2"] = str2
    return o
}