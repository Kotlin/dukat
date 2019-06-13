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

external open class Foo
external var fooKey: dynamic /* String | Foo | Number */ = definedExternally
external fun barKey(a: String): Unit
external fun barKey(a: Foo): Unit
external fun barKey(a: Number): Unit
external fun barList(a: List<dynamic /* String | Foo | Number */>): Unit
external fun barArray(a: Array<dynamic /* String | Foo | Number */>): Unit
external interface Parent {
    @nativeInvoke
    operator fun invoke(vararg children: String): Foo
    @nativeInvoke
    operator fun invoke(vararg children: Foo): Foo
    @nativeInvoke
    operator fun invoke(vararg children: Number): Foo
}
