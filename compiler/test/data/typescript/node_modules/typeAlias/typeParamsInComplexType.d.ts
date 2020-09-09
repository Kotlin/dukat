type Or<T, U> = { f: T } | { g: U }

declare class B {
    f(x: Or<number, string>)
}