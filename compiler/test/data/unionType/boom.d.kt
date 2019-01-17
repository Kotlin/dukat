package boom

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
external fun foo(a: dynamic /* A | B | C | D | E | F | G | H | L | M | N | O | P | R | S | T */, b: Number): Unit = definedExternally
external fun bar(a: A, b: Number): Unit = definedExternally
external fun bar(a: B, b: Number): Unit = definedExternally
external fun bar(a: C, b: Number): Unit = definedExternally
external fun bar(a: D, b: Number): Unit = definedExternally
external fun bar(a: E, b: Number): Unit = definedExternally
external fun bar(a: F, b: Number): Unit = definedExternally
external fun bar(a: G, b: Number): Unit = definedExternally
external fun baz(a: A, b: dynamic /* A | B | C | D | E */): Unit = definedExternally
external fun baz(a: B, b: dynamic /* A | B | C | D | E */): Unit = definedExternally
external fun baz(a: C, b: dynamic /* A | B | C | D | E */): Unit = definedExternally
external fun baz(a: D, b: dynamic /* A | B | C | D | E */): Unit = definedExternally
external fun baz(a: E, b: dynamic /* A | B | C | D | E */): Unit = definedExternally
external fun boo(a: A, b: A, c: A): Unit = definedExternally
external fun boo(a: A, b: A, c: B): Unit = definedExternally
external fun boo(a: A, b: B, c: A): Unit = definedExternally
external fun boo(a: A, b: B, c: B): Unit = definedExternally
external fun boo(a: B, b: A, c: A): Unit = definedExternally
external fun boo(a: B, b: A, c: B): Unit = definedExternally
external fun boo(a: B, b: B, c: A): Unit = definedExternally
external fun boo(a: B, b: B, c: B): Unit = definedExternally
external fun boom(c: String, a: dynamic /* A | B | C | D | E | F | G | H | L | M | N | O | P | R | S | T */, b: dynamic /* A | B | C | D | E | F | G | H | L | M | N | O | P | R | S | T */): Unit = definedExternally
