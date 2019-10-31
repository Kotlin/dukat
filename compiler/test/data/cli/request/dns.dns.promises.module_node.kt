@file:JsQualifier("dns.promises")
@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION")
package dns.promises

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

external fun getServers(): Array<String>

external fun lookup(hostname: String, family: Number): Promise<dns.LookupAddress>

external fun lookup(hostname: String, options: dns.LookupOneOptions): Promise<dns.LookupAddress>

external fun lookup(hostname: String, options: dns.LookupAllOptions): Promise<Array<dns.LookupAddress>>

external fun lookup(hostname: String, options: dns.LookupOptions): Promise<dynamic /* LookupAddress | Array<LookupAddress> */>

external fun lookup(hostname: String): Promise<dns.LookupAddress>

external interface `T$1` {
    var hostname: String
    var service: String
}

external fun lookupService(address: String, port: Number): Promise<`T$1`>

external fun resolve(hostname: String): Promise<Array<String>>

external fun resolve(hostname: String, rrtype: String /* "A" */): Promise<Array<String>>

external fun resolve(hostname: String, rrtype: String /* "AAAA" */): Promise<Array<String>>

external fun resolve(hostname: String, rrtype: String /* "ANY" */): Promise<Array<dynamic /* AnyARecord | AnyAaaaRecord | AnyCnameRecord | AnyMxRecord | AnyNaptrRecord | AnyNsRecord | AnyPtrRecord | AnySoaRecord | AnySrvRecord | AnyTxtRecord */>>

external fun resolve(hostname: String, rrtype: String /* "CNAME" */): Promise<Array<String>>

external fun resolve(hostname: String, rrtype: String /* "MX" */): Promise<Array<dns.MxRecord>>

external fun resolve(hostname: String, rrtype: String /* "NAPTR" */): Promise<Array<dns.NaptrRecord>>

external fun resolve(hostname: String, rrtype: String /* "NS" */): Promise<Array<String>>

external fun resolve(hostname: String, rrtype: String /* "PTR" */): Promise<Array<String>>

external fun resolve(hostname: String, rrtype: String /* "SOA" */): Promise<dns.SoaRecord>

external fun resolve(hostname: String, rrtype: String /* "SRV" */): Promise<Array<dns.SrvRecord>>

external fun resolve(hostname: String, rrtype: String /* "TXT" */): Promise<Array<Array<String>>>

external fun resolve(hostname: String, rrtype: String): Promise<dynamic /* Array<String> | Array<MxRecord> | Array<NaptrRecord> | SoaRecord | Array<SrvRecord> | Array<Array<String>> | Array<dynamic /* AnyARecord | AnyAaaaRecord | AnyCnameRecord | AnyMxRecord | AnyNaptrRecord | AnyNsRecord | AnyPtrRecord | AnySoaRecord | AnySrvRecord | AnyTxtRecord */> */>

external fun resolve4(hostname: String): Promise<Array<String>>

external fun resolve4(hostname: String, options: dns.ResolveWithTtlOptions): Promise<Array<dns.RecordWithTtl>>

external fun resolve4(hostname: String, options: dns.ResolveOptions): Promise<dynamic /* Array<String> | Array<RecordWithTtl> */>

external fun resolve6(hostname: String): Promise<Array<String>>

external fun resolve6(hostname: String, options: dns.ResolveWithTtlOptions): Promise<Array<dns.RecordWithTtl>>

external fun resolve6(hostname: String, options: dns.ResolveOptions): Promise<dynamic /* Array<String> | Array<RecordWithTtl> */>

external fun resolveAny(hostname: String): Promise<Array<dynamic /* AnyARecord | AnyAaaaRecord | AnyCnameRecord | AnyMxRecord | AnyNaptrRecord | AnyNsRecord | AnyPtrRecord | AnySoaRecord | AnySrvRecord | AnyTxtRecord */>>

external fun resolveCname(hostname: String): Promise<Array<String>>

external fun resolveMx(hostname: String): Promise<Array<dns.MxRecord>>

external fun resolveNaptr(hostname: String): Promise<Array<dns.NaptrRecord>>

external fun resolveNs(hostname: String): Promise<Array<String>>

external fun resolvePtr(hostname: String): Promise<Array<String>>

external fun resolveSoa(hostname: String): Promise<dns.SoaRecord>

external fun resolveSrv(hostname: String): Promise<Array<dns.SrvRecord>>

external fun resolveTxt(hostname: String): Promise<Array<Array<String>>>

external fun reverse(ip: String): Promise<Array<String>>

external fun setServers(servers: ReadonlyArray<String>)

external open class Resolver {
    open var getServers: Any
    open var resolve: Any
    open var resolve4: Any
    open var resolve6: Any
    open var resolveAny: Any
    open var resolveCname: Any
    open var resolveMx: Any
    open var resolveNaptr: Any
    open var resolveNs: Any
    open var resolvePtr: Any
    open var resolveSoa: Any
    open var resolveSrv: Any
    open var resolveTxt: Any
    open var reverse: Any
    open var setServers: Any
}