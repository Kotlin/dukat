
external interface WebGLContextAttributes {
    var alpha: Boolean? get() = definedExternally; set(value) = definedExternally
    var depth: Boolean? get() = definedExternally; set(value) = definedExternally
    var stencil: Boolean? get() = definedExternally; set(value) = definedExternally
    var antialias: Boolean? get() = definedExternally; set(value) = definedExternally
    var premultipliedAlpha: Boolean? get() = definedExternally; set(value) = definedExternally
    var preserveDrawingBuffer: Boolean? get() = definedExternally; set(value) = definedExternally
    var preferLowPowerToHighPerformance: Boolean? get() = definedExternally; set(value) = definedExternally
    var failIfMajorPerformanceCaveat: Boolean? get() = definedExternally; set(value) = definedExternally
}
@kotlin.internal.InlineOnly
inline fun WebGLContextAttributes(alpha: Boolean? = true, depth: Boolean? = true, stencil: Boolean? = false, antialias: Boolean? = true, premultipliedAlpha: Boolean? = true, preserveDrawingBuffer: Boolean? = false, preferLowPowerToHighPerformance: Boolean? = false, failIfMajorPerformanceCaveat: Boolean? = false): WebGLContextAttributes { val o = js("({})"); o["alpha"] = alpha; o["depth"] = depth; o["stencil"] = stencil; o["antialias"] = antialias; o["premultipliedAlpha"] = premultipliedAlpha; o["preserveDrawingBuffer"] = preserveDrawingBuffer; o["preferLowPowerToHighPerformance"] = preferLowPowerToHighPerformance; o["failIfMajorPerformanceCaveat"] = failIfMajorPerformanceCaveat; return o }