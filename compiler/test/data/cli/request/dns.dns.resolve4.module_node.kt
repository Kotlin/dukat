@file:JsQualifier("dns.resolve4")
@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package dns.resolve4

import kotlin.js.*
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

external fun __promisify__(hostname: String): Promise<Array<String>>

external fun __promisify__(hostname: String, options: dns.ResolveWithTtlOptions): Promise<Array<dns.RecordWithTtl>>

external fun __promisify__(hostname: String, options: dns.ResolveOptions = definedExternally /* null */): Promise<dynamic /* Array<String> | Array<RecordWithTtl> */>