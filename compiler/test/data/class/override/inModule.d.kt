@file:JsQualifier("TypeScript.Syntax")
package inModule.TypeScript.Syntax

external interface IFactory {
    fun sourceUnit(moduleElements: ISyntaxList, endOfFileToken: ISyntaxToken): SourceUnitSyntax
}
external open class NormalModeFactory : IFactory {
    override fun sourceUnit(moduleElements: ISyntaxList, endOfFileToken: ISyntaxToken): SourceUnitSyntax = definedExternally
}
