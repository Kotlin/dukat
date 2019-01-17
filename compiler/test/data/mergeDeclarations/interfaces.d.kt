@file:JsQualifier("_")
package interfaces.`_`

external interface LoDashStatic {
    fun chain(value: Number): LoDashWrapper<Number>
    fun chain(value: String): LoDashWrapper<String>
    fun chain(value: Boolean): LoDashWrapper<Boolean>
    fun <T> chain(value: Array<T>): LoDashArrayWrapper<T>
    fun chain(value: Any): LoDashWrapper<Any>
    fun <T> compact(array: Array<T>): Array<T>
    fun <T> compact(array: List<T>): Array<T>
}
