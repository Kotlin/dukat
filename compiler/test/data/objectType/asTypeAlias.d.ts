type I = {
    foo(): string
}
type J = {
    foo(): string
}
type K = {
    bar(): number
}

type Q = {}
type W = {}

declare function f(a: I, b: J, c: I, q: Q, w:W): K

declare var x: I;
declare var y: I;
declare var z: J;
declare var q: Q;
declare var w: W;
