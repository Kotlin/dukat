package module

@JsModule("lodash")
external val `_`: module.`_`.LoDashStatic = definedExternally

// ------------------------------------------------------------------------------------------
@file:JsQualifier("_")
package module.`_`

external interface LoDashStatic {
    @nativeInvoke
    operator fun invoke(value: Number): LoDashArrayWrapper<Number>
    var VERSION: String
    var support: Support
}
external interface Support {
    var argsClass: Boolean
    var argsObject: Boolean
}
external interface LoDashArrayWrapper<T> {
    fun difference(vararg others: Array<T>): LoDashArrayWrapper<T>
    fun difference(vararg others: List<T>): LoDashArrayWrapper<T>
}
