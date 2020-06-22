// [test] overrideMethodOfParentOfParent.TypeScript.kt
@file:JsQualifier("TypeScript")
@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION")
package TypeScript

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

external interface ISyntaxElement {
    fun kind(): SyntaxKind
}

external interface ISyntaxList : ISyntaxElement {
    fun childAt(index: Number): ISyntaxNodeOrToken
    fun toArray(): Array<ISyntaxNodeOrToken>
    fun insertChildrenInto(array: Array<ISyntaxElement>, index: Number)
}

external interface ISyntaxNodeOrToken

external interface SyntaxKind

// ------------------------------------------------------------------------------------------
// [test] overrideMethodOfParentOfParent.TypeScript.Syntax.kt
@file:JsQualifier("TypeScript.Syntax")
@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION")
package TypeScript.Syntax

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
import TypeScript.SyntaxKind
import TypeScript.ISyntaxNodeOrToken
import TypeScript.ISyntaxElement
import TypeScript.ISyntaxList

external open class EmptySyntaxList : ISyntaxList {
    override fun kind(): SyntaxKind
    override fun childAt(index: Number): ISyntaxNodeOrToken
    override fun toArray(): Array<ISyntaxNodeOrToken>
    override fun insertChildrenInto(array: Array<ISyntaxElement>, index: Number)
}