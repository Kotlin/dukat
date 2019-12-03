@file:JsQualifier("AceAjax")
@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION")
package AceAjax

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

@Suppress("NESTED_CLASS_IN_EXTERNAL_INTERFACE")
external interface KeyBinding {
    fun setDefaultHandler(kb: Any)
    fun setKeyboardHandler(kb: Any)
    fun addKeyboardHandler(kb: Any, pos: Any)
    fun removeKeyboardHandler(kb: Any): Boolean
    fun getKeyboardHandler(): Any
    fun onCommandKey(e: Any, hashId: Any, keyCode: Any)
    fun onTextInput(text: Any)

    companion object : Foo by definedExternally
}

external interface Foo {
    fun foo(editor: Editor): Boolean
}

external interface Editor