
import kotlin.js.*

external interface WebGLContextAttributes {
    var alpha: Boolean? /* = true */ get() = definedExternally; set(value) = definedExternally
    var depth: Boolean? /* = true */ get() = definedExternally; set(value) = definedExternally
    var stencil: Boolean? /* = false */ get() = definedExternally; set(value) = definedExternally
    var antialias: Boolean? /* = true */ get() = definedExternally; set(value) = definedExternally
    var premultipliedAlpha: Boolean? /* = true */ get() = definedExternally; set(value) = definedExternally
    var preserveDrawingBuffer: Boolean? /* = false */ get() = definedExternally; set(value) = definedExternally
    var preferLowPowerToHighPerformance: Boolean? /* = false */ get() = definedExternally; set(value) = definedExternally
    var failIfMajorPerformanceCaveat: Boolean? /* = false */ get() = definedExternally; set(value) = definedExternally
}
@kotlin.internal.InlineOnly
inline fun WebGLContextAttributes(alpha: Boolean? = true, depth: Boolean? = true, stencil: Boolean? = false, antialias: Boolean? = true, premultipliedAlpha: Boolean? = true, preserveDrawingBuffer: Boolean? = false, preferLowPowerToHighPerformance: Boolean? = false, failIfMajorPerformanceCaveat: Boolean? = false): WebGLContextAttributes { val o = js("({})"); o["alpha"] = alpha; o["depth"] = depth; o["stencil"] = stencil; o["antialias"] = antialias; o["premultipliedAlpha"] = premultipliedAlpha; o["preserveDrawingBuffer"] = preserveDrawingBuffer; o["preferLowPowerToHighPerformance"] = preferLowPowerToHighPerformance; o["failIfMajorPerformanceCaveat"] = failIfMajorPerformanceCaveat; return o }
external interface PointerEventInit {
    var pointerId: Int? /* = 0 */ get() = definedExternally; set(value) = definedExternally
    var width: Double? /* = 1.0 */ get() = definedExternally; set(value) = definedExternally
    var height: Double? /* = 1.0 */ get() = definedExternally; set(value) = definedExternally
    var pressure: Float? /* = 0f */ get() = definedExternally; set(value) = definedExternally
    var tangentialPressure: Float? /* = 0f */ get() = definedExternally; set(value) = definedExternally
    var tiltX: Int? /* = 0 */ get() = definedExternally; set(value) = definedExternally
    var tiltY: Int? /* = 0 */ get() = definedExternally; set(value) = definedExternally
    var twist: Int? /* = 0 */ get() = definedExternally; set(value) = definedExternally
    var pointerType: String? /* = "" */ get() = definedExternally; set(value) = definedExternally
    var isPrimary: Boolean? /* = false */ get() = definedExternally; set(value) = definedExternally
}
@kotlin.internal.InlineOnly
inline fun PointerEventInit(pointerId: Int? = 0, width: Double? = 1.0, height: Double? = 1.0, pressure: Float? = 0f, tangentialPressure: Float? = 0f, tiltX: Int? = 0, tiltY: Int? = 0, twist: Int? = 0, pointerType: String? = "", isPrimary: Boolean? = false): PointerEventInit { val o = js("({})"); o["pointerId"] = pointerId; o["width"] = width; o["height"] = height; o["pressure"] = pressure; o["tangentialPressure"] = tangentialPressure; o["tiltX"] = tiltX; o["tiltY"] = tiltY; o["twist"] = twist; o["pointerType"] = pointerType; o["isPrimary"] = isPrimary; return o }
external abstract class A {
    open var attributes: WebGLContextAttributes
}
external interface UndefinedMemberDictionary {
    var str: String? get() = definedExternally; set(value) = definedExternally
    var str2: String? get() = definedExternally; set(value) = definedExternally
}
@kotlin.internal.InlineOnly
inline fun UndefinedMemberDictionary(str: String?, str2: String? = undefined): UndefinedMemberDictionary { val o = js("({})"); o["str"] = str; o["str2"] = str2; return o }