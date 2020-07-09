// [test] genericWithDefaultValue.kt
@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")

import kotlin.js.*
import kotlin.js.Json
import org.khronos.webgl.*
import org.w3c.dom.*
import org.w3c.dom.events.*
import org.w3c.dom.parsing.*
import org.w3c.dom.svg.*
import org.w3c.dom.url.*
import org.w3c.fetch.*
import org.w3c.files.*
import org.w3c.notifications.*
import org.w3c.performance.*
import org.w3c.workers.*
import org.w3c.xhr.*

external interface Chain<T, V> {
    fun shuffle(): Chain__1<T>
    fun shuffleString(): Chain__1<String>
    fun shuffleLambda(): Chain__1<() -> String?>
}

external interface Chain__1<T> : Chain<T, T>

external interface ChainOfArrays<G> : Chain__1<Array<G>> {
    fun flatten(shallow: Boolean = definedExternally): Chain__1<G>
}

external interface AsyncResultObjectCallback<T, E> {
    @nativeInvoke
    operator fun invoke(err: E?, results: Array<T?>)
}

external interface `T$0`<T> {
    @nativeGetter
    operator fun get(key: String): T?
    @nativeSetter
    operator fun set(key: String, value: T)
}

external interface `T$1`<R> {
    @nativeGetter
    operator fun get(key: String): R?
    @nativeSetter
    operator fun set(key: String, value: R)
}

external fun <T, R, E> transform(arr: `T$0`<T>, iteratee: (acc: `T$1`<R>, item: T, key: String, callback: (error: E) -> Unit) -> Unit, callback: AsyncResultObjectCallback<T, E> = definedExternally)