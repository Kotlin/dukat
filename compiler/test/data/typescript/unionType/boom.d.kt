// [test] boom.kt
@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")

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

external interface A

external interface B

external interface C

external interface D

external interface E

external interface F

external interface G

external interface H

external interface L

external interface M

external interface N

external interface O

external interface P

external interface R

external interface S

external interface T

external fun foo(a: dynamic /* A | B | C | D | E | F | G | H | L | M | N | O | P | R | S | T */, b: Number)

external fun bar(a: A, b: Number)

external fun bar(a: B, b: Number)

external fun bar(a: C, b: Number)

external fun bar(a: D, b: Number)

external fun bar(a: E, b: Number)

external fun bar(a: F, b: Number)

external fun bar(a: G, b: Number)

external fun baz(a: A, b: dynamic /* A | B | C | D | E */)

external fun baz(a: B, b: dynamic /* A | B | C | D | E */)

external fun baz(a: C, b: dynamic /* A | B | C | D | E */)

external fun baz(a: D, b: dynamic /* A | B | C | D | E */)

external fun baz(a: E, b: dynamic /* A | B | C | D | E */)

external fun boo(a: A, b: A, c: A)

external fun boo(a: A, b: A, c: B)

external fun boo(a: A, b: B, c: A)

external fun boo(a: A, b: B, c: B)

external fun boo(a: B, b: A, c: A)

external fun boo(a: B, b: A, c: B)

external fun boo(a: B, b: B, c: A)

external fun boo(a: B, b: B, c: B)

external fun boom(c: String, a: dynamic /* A | B | C | D | E | F | G | H | L | M | N | O | P | R | S | T */, b: dynamic /* A | B | C | D | E | F | G | H | L | M | N | O | P | R | S | T */)