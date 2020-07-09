@file:JsQualifier("crypto.generateKeyPair")
@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package crypto.generateKeyPair

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

external interface `T$6` {
    var publicKey: String
    var privateKey: String
}

external fun __promisify__(type: String /* "rsa" */, options: crypto.RSAKeyPairOptions): Promise<`T$6`>

external interface `T$7` {
    var publicKey: String
    var privateKey: Buffer
}

external fun __promisify__(type: String /* "rsa" */, options: crypto.RSAKeyPairOptions): Promise<`T$7`>

external interface `T$8` {
    var publicKey: Buffer
    var privateKey: String
}

external fun __promisify__(type: String /* "rsa" */, options: crypto.RSAKeyPairOptions): Promise<`T$8`>

external interface `T$9` {
    var publicKey: Buffer
    var privateKey: Buffer
}

external fun __promisify__(type: String /* "rsa" */, options: crypto.RSAKeyPairOptions): Promise<`T$9`>

external fun __promisify__(type: String /* "rsa" */, options: crypto.RSAKeyPairKeyObjectOptions): Promise<crypto.KeyPairKeyObjectResult>

external fun __promisify__(type: String /* "dsa" */, options: crypto.DSAKeyPairOptions): Promise<`T$6`>

external fun __promisify__(type: String /* "dsa" */, options: crypto.DSAKeyPairOptions): Promise<`T$7`>

external fun __promisify__(type: String /* "dsa" */, options: crypto.DSAKeyPairOptions): Promise<`T$8`>

external fun __promisify__(type: String /* "dsa" */, options: crypto.DSAKeyPairOptions): Promise<`T$9`>

external fun __promisify__(type: String /* "dsa" */, options: crypto.DSAKeyPairKeyObjectOptions): Promise<crypto.KeyPairKeyObjectResult>

external fun __promisify__(type: String /* "ec" */, options: crypto.ECKeyPairOptions): Promise<`T$6`>

external fun __promisify__(type: String /* "ec" */, options: crypto.ECKeyPairOptions): Promise<`T$7`>

external fun __promisify__(type: String /* "ec" */, options: crypto.ECKeyPairOptions): Promise<`T$8`>

external fun __promisify__(type: String /* "ec" */, options: crypto.ECKeyPairOptions): Promise<`T$9`>

external fun __promisify__(type: String /* "ec" */, options: crypto.ECKeyPairKeyObjectOptions): Promise<crypto.KeyPairKeyObjectResult>