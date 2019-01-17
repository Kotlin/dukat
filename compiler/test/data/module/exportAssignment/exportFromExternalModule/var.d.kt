@file:JsQualifier("Mixto")
package `var`.Mixto

external interface IMixinStatic {
    fun includeInto(constructor: Any)
    fun extend(`object`: Any)
}

// ------------------------------------------------------------------------------------------
package `var`.mixto

@JsModule("mixto")
external val mixto: Mixto.IMixinStatic = definedExternally
