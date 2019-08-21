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

external interface Shape

external interface Box : Shape

external interface BaseEvent {
    var data: dynamic /* String | Number */
    fun getDelegateTarget(): Shape
    fun getElement(): Element
    fun <T : Shape> transform(shape: T? = definedExternally /* null */): T
    var prop: Any
    fun queryByReturnType(query: String, parameters: Array<Any>? = definedExternally /* null */): Promise<Any>
}

external open class BoxStringEvent : BaseEvent {
    override var data: String
    override fun getDelegateTarget(): Box
    override fun getElement(): HTMLElement
    override fun <T : Shape> transform(shape: T?): T
    override var prop: String
    override fun queryByReturnType(query: String, parameters: Array<Any>?): Promise<String>
}

external interface NumberEvent : BaseEvent {
    override var data: Number
    var otherProp: Any
}

external open class ParentClass {
    open var prop: Any
}

external open class ChildClass : ParentClass {
    override var prop: String
}