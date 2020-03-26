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

external interface AppEvent : Event {
    override var currentTarget: Element?
        get() = definedExternally
        set(value) = definedExternally
    override var target: Element?
        get() = definedExternally
        set(value) = definedExternally
    fun preventDefault(): Any
}

external interface LibEvent : Event {
    override fun preventDefault()
}

external open class NativeEvent : Event {
    open fun preventDefault(): Any
}

external open class FrameworkEvent : Event {
    override fun preventDefault()
}