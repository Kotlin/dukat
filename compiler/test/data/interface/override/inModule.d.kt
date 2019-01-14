@file:JsQualifier("TypeScript")
package inModule.TypeScript

external interface ISyntaxElement {
    fun childAt(index: Number): ISyntaxElement
}
external interface ISeparatedSyntaxList : ISyntaxElement {
    override fun childAt(index: Number): ISeparatedSyntaxList
}
