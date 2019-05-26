@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION", "NESTED_CLASS_IN_EXTERNAL_INTERFACE")
package inClassLike

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
    open var self: MyClass /* this */ = definedExternally
    open fun that(): MyClass /* this */ = definedExternally
    open fun load(ids: String, handler: (self: MyClass /* this */, args: Array<Any>) -> Unit): Unit = definedExternally
}
external interface MyInterface {
    var self: MyInterface /* this */
    fun that(): MyInterface /* this */
    fun load(ids: String, handler: (self: MyInterface /* this */, args: Array<Any>) -> Unit)
}
