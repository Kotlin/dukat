declare interface A {
}
declare interface B {
}
declare interface C {
}
declare interface D {
}
declare interface E {
}
declare interface F {
}
declare interface G {
}
declare interface H {
}
declare interface L {
}
declare interface M {
}
declare interface N {
}
declare interface O {
}
declare interface P {
}
declare interface R {
}
declare interface S {
}
declare interface T {
}

type T2 = A | B
type T5 = A | B | C | D | E
type T7 = A | B | C | D | E | F | G
type TM = A | B | C | D | E | F | G | H | L | M | N | O | P | R | S | T | F

declare function foo(a: TM, b: number)
declare function bar(a: T7, b: number)
declare function baz(a: T5, b: T5)
declare function boo(a: T2, b: T2, c: T2)
declare function boom(c: string, a: TM, b: TM)