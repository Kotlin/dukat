@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION", "NESTED_CLASS_IN_EXTERNAL_INTERFACE")

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

external open class BoxStringEvent : BaseEvent {
    override var data: String = definedExternally
    override fun getDelegateTarget(): Box = definedExternally
    override fun getElement(): HTMLElement = definedExternally
    override fun <T : Shape> transform(shape: T?): T = definedExternally
    override fun getSortOfEventTarget(): SortOfElement = definedExternally
    override var prop: String = definedExternally
}
external interface NumberEvent : BaseEvent {
    override var data: Number
    var otherProp: Any
}
