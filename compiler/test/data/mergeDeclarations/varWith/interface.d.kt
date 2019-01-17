@file:JsQualifier("AceAjax")
package `interface`.AceAjax

external interface KeyBinding {
    fun setDefaultHandler(kb: Any)
    fun setKeyboardHandler(kb: Any)
    fun addKeyboardHandler(kb: Any, pos: Any)
    fun removeKeyboardHandler(kb: Any): Boolean
    fun getKeyboardHandler(): Any
    fun onCommandKey(e: Any, hashId: Any, keyCode: Any)
    fun onTextInput(text: Any)
    companion object : Foo by definedExternally {
    }
}
external interface Foo {
    fun foo(editor: Editor): Boolean
}
