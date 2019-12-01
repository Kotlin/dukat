@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION")

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

external open class MyClass {
    open var self: MyClass /* this */
    open fun that(): MyClass /* this */
    open fun load(ids: String, handler: (self: MyClass /* this */, args: Array<Any>) -> Unit)
}

external interface MyInterface {
    var self: MyInterface /* this */
    fun that(): MyInterface /* this */
    fun load(ids: String, handler: (self: MyInterface /* this */, args: Array<Any>) -> Unit)
}

external interface WeaklikeMaplike<K : Any?, V> {
    fun delete(key: K): Boolean
    fun get(key: K): V?
    fun has(key: K): Boolean
    fun set(key: K, value: V): WeaklikeMaplike<K, V> /* this */
}