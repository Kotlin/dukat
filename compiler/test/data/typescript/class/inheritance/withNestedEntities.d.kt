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
import internal.EventEmitter
import internal.Computable

external interface BasicEmitter {
    fun emit(event: String, vararg args: Any): Boolean
    fun emit(event: Any, vararg args: Any): Boolean
}

@Suppress("NESTED_CLASS_IN_EXTERNAL_INTERFACE")
external interface internal : BasicEmitter {
    open class EventEmitter : internal {
        override fun emit(event: String, vararg args: Any): Boolean
        override fun emit(event: Any, vararg args: Any): Boolean
    }
    interface Computable {
        fun compute(): Number
    }
}

external interface ChildProcess : EventEmitter {
    override fun emit(event: String, vararg args: Any): Boolean
    override fun emit(event: Any, vararg args: Any): Boolean
}

external open class ComputableEntity : Computable {
    override fun compute(): Number
}