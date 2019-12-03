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
import MyFramework.MyEvent

@Suppress("NESTED_CLASS_IN_EXTERNAL_INTERFACE")
external interface MyFramework<S, T> {
    fun triggerHandler(source: S, eventType_event: MyEvent): T
    interface MyEvent {
        fun ping()
    }
    open class MyClass<T : Number> {
        open fun ping(): T
    }

    companion object {
        var prop: String
    }
}