package withGenericParams.Q

inline fun <T, B> Promise<T>.foo(b: B): T { return this.asDynamic().foo(b) }
inline fun <T0, T, B> Promise<T0>.foo(a: Any, b: B): T { return this.asDynamic().foo(a, b) }
inline var <T> Promise<T>.bar: Array<T> get() = this.asDynamic().bar; set(value) { this.asDynamic().bar = value }

// ------------------------------------------------------------------------------------------
@file:JsModule("ref-array")
package withGenericParams.ref_array

external interface ArrayType<T> {
    @nativeGetter
    operator fun get(prop: String): Number?
    @nativeSetter
    operator fun set(prop: String, value: Number)
    var someField: String
    var optionalField: T? get() = definedExternally; set(value) = definedExternally
    @nativeInvoke
    operator fun invoke(resourceId: String, hash: Any? = definedExternally /* null */, callback: Function<*>? = definedExternally /* null */)
}
