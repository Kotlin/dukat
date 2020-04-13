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

typealias Numba = Number

typealias NumArray = Array<Numba>

external interface Processor {
    fun process(input: NumArray): Array<Number>
}

typealias AliasedProcessor = Processor

external open class MultithreadedProcessor : AliasedProcessor {
    override fun process(input: Array<Number>): Array<Numba>
}

external open class SinglethreadedProcessor : AliasedProcessor {
    override fun process(input: Array<Number>): NumArray
}

typealias NumCollection<T> = Array<T>

external interface SomethingStatic {
    fun <T : Any?, U : Any> pick(obj: T, vararg props: dynamic /* typealias PropertyPath = dynamic */): Any
}