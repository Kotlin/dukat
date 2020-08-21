@file:JsQualifier("dns.resolve")
@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package dns.resolve

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

external fun __promisify__(hostname: String, rrtype: String /* "A" */ = definedExternally /* null */): Promise<Array<String>>

external fun __promisify__(hostname: String, rrtype: String /* "ANY" */): Promise<Array<dynamic /* AnyARecord | AnyAaaaRecord | AnyCnameRecord | AnyMxRecord | AnyNaptrRecord | AnyNsRecord | AnyPtrRecord | AnySoaRecord | AnySrvRecord | AnyTxtRecord */>>

external fun __promisify__(hostname: String, rrtype: String /* "MX" */): Promise<Array<dns.MxRecord>>

external fun __promisify__(hostname: String, rrtype: String /* "NAPTR" */): Promise<Array<dns.NaptrRecord>>

external fun __promisify__(hostname: String, rrtype: String /* "SOA" */): Promise<dns.SoaRecord>

external fun __promisify__(hostname: String, rrtype: String /* "SRV" */): Promise<Array<dns.SrvRecord>>

external fun __promisify__(hostname: String, rrtype: String /* "TXT" */): Promise<Array<Array<String>>>

external fun __promisify__(hostname: String, rrtype: String): Promise<dynamic /* Array<String> | Array<MxRecord> | Array<NaptrRecord> | SoaRecord | Array<SrvRecord> | Array<Array<String>> | Array<dynamic /* AnyARecord | AnyAaaaRecord | AnyCnameRecord | AnyMxRecord | AnyNaptrRecord | AnyNsRecord | AnyPtrRecord | AnySoaRecord | AnySrvRecord | AnyTxtRecord */> */>