@file:JsQualifier("dns.lookup")
@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package dns.lookup

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

external fun __promisify__(hostname: String, options: dns.LookupAllOptions): Promise<Array<dns.LookupAddress>>

external fun __promisify__(hostname: String, options: dns.LookupOneOptions = definedExternally /* null */): Promise<dns.LookupAddress>

external fun __promisify__(hostname: String, options: Number? = definedExternally /* null */): Promise<dns.LookupAddress>

external fun __promisify__(hostname: String, options: dns.LookupOptions): Promise<dynamic /* LookupAddress | Array<LookupAddress> */>

external fun __promisify__(hostname: String): Promise<dns.LookupAddress>