@file:JsModule("dns")
@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package dns

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

external var ADDRCONFIG: Number

external var V4MAPPED: Number

external interface LookupOptions {
    var family: Number?
        get() = definedExternally
        set(value) = definedExternally
    var hints: Number?
        get() = definedExternally
        set(value) = definedExternally
    var all: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var verbatim: Boolean?
        get() = definedExternally
        set(value) = definedExternally
}

external interface LookupOneOptions : LookupOptions {
    var all: String /* false */
}

external interface LookupAllOptions : LookupOptions {
    var all: String /* true */
}

external interface LookupAddress {
    var address: String
    var family: Number
}

external fun lookup(hostname: String, family: Number, callback: (err: NodeJS.ErrnoException?, address: String, family: Number) -> Unit)

external fun lookup(hostname: String, options: LookupOneOptions, callback: (err: NodeJS.ErrnoException?, address: String, family: Number) -> Unit)

external fun lookup(hostname: String, options: LookupAllOptions, callback: (err: NodeJS.ErrnoException?, addresses: Array<LookupAddress>) -> Unit)

external fun lookup(hostname: String, options: LookupOptions, callback: (err: NodeJS.ErrnoException?, address: dynamic /* String | Array<LookupAddress> */, family: Number) -> Unit)

external fun lookup(hostname: String, callback: (err: NodeJS.ErrnoException?, address: String, family: Number) -> Unit)

external fun lookupService(address: String, port: Number, callback: (err: NodeJS.ErrnoException?, hostname: String, service: String) -> Unit)

external interface ResolveOptions {
    var ttl: Boolean
}

external interface ResolveWithTtlOptions : ResolveOptions {
    var ttl: String /* true */
}

external interface RecordWithTtl {
    var address: String
    var ttl: Number
}

external interface AnyARecord : RecordWithTtl {
    var type: String /* "A" */
}

external interface AnyAaaaRecord : RecordWithTtl {
    var type: String /* "AAAA" */
}

external interface MxRecord {
    var priority: Number
    var exchange: String
}

external interface AnyMxRecord : MxRecord {
    var type: String /* "MX" */
}

external interface NaptrRecord {
    var flags: String
    var service: String
    var regexp: String
    var replacement: String
    var order: Number
    var preference: Number
}

external interface AnyNaptrRecord : NaptrRecord {
    var type: String /* "NAPTR" */
}

external interface SoaRecord {
    var nsname: String
    var hostmaster: String
    var serial: Number
    var refresh: Number
    var retry: Number
    var expire: Number
    var minttl: Number
}

external interface AnySoaRecord : SoaRecord {
    var type: String /* "SOA" */
}

external interface SrvRecord {
    var priority: Number
    var weight: Number
    var port: Number
    var name: String
}

external interface AnySrvRecord : SrvRecord {
    var type: String /* "SRV" */
}

external interface AnyTxtRecord {
    var type: String /* "TXT" */
    var entries: Array<String>
}

external interface AnyNsRecord {
    var type: String /* "NS" */
    var value: String
}

external interface AnyPtrRecord {
    var type: String /* "PTR" */
    var value: String
}

external interface AnyCnameRecord {
    var type: String /* "CNAME" */
    var value: String
}

external fun resolve(hostname: String, callback: (err: NodeJS.ErrnoException?, addresses: Array<String>) -> Unit)

external fun resolve(hostname: String, rrtype: String /* "A" */, callback: (err: NodeJS.ErrnoException?, addresses: Array<String>) -> Unit)

external fun resolve(hostname: String, rrtype: String /* "AAAA" */, callback: (err: NodeJS.ErrnoException?, addresses: Array<String>) -> Unit)

external fun resolve(hostname: String, rrtype: String /* "ANY" */, callback: (err: NodeJS.ErrnoException?, addresses: Array<dynamic /* AnyARecord | AnyAaaaRecord | AnyCnameRecord | AnyMxRecord | AnyNaptrRecord | AnyNsRecord | AnyPtrRecord | AnySoaRecord | AnySrvRecord | AnyTxtRecord */>) -> Unit)

external fun resolve(hostname: String, rrtype: String /* "CNAME" */, callback: (err: NodeJS.ErrnoException?, addresses: Array<String>) -> Unit)

external fun resolve(hostname: String, rrtype: String /* "MX" */, callback: (err: NodeJS.ErrnoException?, addresses: Array<MxRecord>) -> Unit)

external fun resolve(hostname: String, rrtype: String /* "NAPTR" */, callback: (err: NodeJS.ErrnoException?, addresses: Array<NaptrRecord>) -> Unit)

external fun resolve(hostname: String, rrtype: String /* "NS" */, callback: (err: NodeJS.ErrnoException?, addresses: Array<String>) -> Unit)

external fun resolve(hostname: String, rrtype: String /* "PTR" */, callback: (err: NodeJS.ErrnoException?, addresses: Array<String>) -> Unit)

external fun resolve(hostname: String, rrtype: String /* "SOA" */, callback: (err: NodeJS.ErrnoException?, addresses: SoaRecord) -> Unit)

external fun resolve(hostname: String, rrtype: String /* "SRV" */, callback: (err: NodeJS.ErrnoException?, addresses: Array<SrvRecord>) -> Unit)

external fun resolve(hostname: String, rrtype: String /* "TXT" */, callback: (err: NodeJS.ErrnoException?, addresses: Array<Array<String>>) -> Unit)

external fun resolve(hostname: String, rrtype: String, callback: (err: NodeJS.ErrnoException?, addresses: dynamic /* Array<String> | Array<MxRecord> | Array<NaptrRecord> | SoaRecord | Array<SrvRecord> | Array<Array<String>> | Array<dynamic /* AnyARecord | AnyAaaaRecord | AnyCnameRecord | AnyMxRecord | AnyNaptrRecord | AnyNsRecord | AnyPtrRecord | AnySoaRecord | AnySrvRecord | AnyTxtRecord */> */) -> Unit)

external fun resolve4(hostname: String, callback: (err: NodeJS.ErrnoException?, addresses: Array<String>) -> Unit)

external fun resolve4(hostname: String, options: ResolveWithTtlOptions, callback: (err: NodeJS.ErrnoException?, addresses: Array<RecordWithTtl>) -> Unit)

external fun resolve4(hostname: String, options: ResolveOptions, callback: (err: NodeJS.ErrnoException?, addresses: dynamic /* Array<String> | Array<RecordWithTtl> */) -> Unit)

external fun resolve6(hostname: String, callback: (err: NodeJS.ErrnoException?, addresses: Array<String>) -> Unit)

external fun resolve6(hostname: String, options: ResolveWithTtlOptions, callback: (err: NodeJS.ErrnoException?, addresses: Array<RecordWithTtl>) -> Unit)

external fun resolve6(hostname: String, options: ResolveOptions, callback: (err: NodeJS.ErrnoException?, addresses: dynamic /* Array<String> | Array<RecordWithTtl> */) -> Unit)

external fun resolveCname(hostname: String, callback: (err: NodeJS.ErrnoException?, addresses: Array<String>) -> Unit)

external fun resolveMx(hostname: String, callback: (err: NodeJS.ErrnoException?, addresses: Array<MxRecord>) -> Unit)

external fun resolveNaptr(hostname: String, callback: (err: NodeJS.ErrnoException?, addresses: Array<NaptrRecord>) -> Unit)

external fun resolveNs(hostname: String, callback: (err: NodeJS.ErrnoException?, addresses: Array<String>) -> Unit)

external fun resolvePtr(hostname: String, callback: (err: NodeJS.ErrnoException?, addresses: Array<String>) -> Unit)

external fun resolveSoa(hostname: String, callback: (err: NodeJS.ErrnoException?, address: SoaRecord) -> Unit)

external fun resolveSrv(hostname: String, callback: (err: NodeJS.ErrnoException?, addresses: Array<SrvRecord>) -> Unit)

external fun resolveTxt(hostname: String, callback: (err: NodeJS.ErrnoException?, addresses: Array<Array<String>>) -> Unit)

external fun resolveAny(hostname: String, callback: (err: NodeJS.ErrnoException?, addresses: Array<dynamic /* AnyARecord | AnyAaaaRecord | AnyCnameRecord | AnyMxRecord | AnyNaptrRecord | AnyNsRecord | AnyPtrRecord | AnySoaRecord | AnySrvRecord | AnyTxtRecord */>) -> Unit)

external fun reverse(ip: String, callback: (err: NodeJS.ErrnoException?, hostnames: Array<String>) -> Unit)

external fun setServers(servers: ReadonlyArray<String>)

external fun getServers(): Array<String>

external var NODATA: String

external var FORMERR: String

external var SERVFAIL: String

external var NOTFOUND: String

external var NOTIMP: String

external var REFUSED: String

external var BADQUERY: String

external var BADNAME: String

external var BADFAMILY: String

external var BADRESP: String

external var CONNREFUSED: String

external var TIMEOUT: String

external var EOF: String

external var FILE: String

external var NOMEM: String

external var DESTRUCTION: String

external var BADSTR: String

external var BADFLAGS: String

external var NONAME: String

external var BADHINTS: String

external var NOTINITIALIZED: String

external var LOADIPHLPAPI: String

external var ADDRGETNETWORKPARAMS: String

external var CANCELLED: String

external open class Resolver {
    open var getServers: Any
    open var setServers: Any
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
    open fun cancel()
}