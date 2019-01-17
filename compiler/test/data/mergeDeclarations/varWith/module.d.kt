package module

@JsModule("lodash")
external val `_`: `_`.LoDashStatic = definedExternally

// ------------------------------------------------------------------------------------------
@file:JsModule("lodash")
package module.`_`

external interface LoDashStatic {
    @nativeInvoke
    operator fun invoke(value: Number): LoDashWrapper<Number>
    var VERSION: String
    var support: `_`.Support
}
external interface Support {
    var argsClass: Boolean
    var argsObject: Boolean
}
external interface LoDashArrayWrapper<T> {
    fun difference(vararg others: Array<T>): `_`.LoDashArrayWrapper<T>
    fun difference(vararg others: List<T>): `_`.LoDashArrayWrapper<T>
}
