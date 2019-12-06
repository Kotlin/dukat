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

external interface Foo<T> {
    val methodWithOutArgs: (() -> Unit)?
        get() = definedExternally
    val <A> methodWithString: ((s: A) -> T)?
        get() = definedExternally
    val <A : T, B> methodWithManyArgs: ((n: A, settings: Bar) -> B)?
        get() = definedExternally
}

external interface Bar
