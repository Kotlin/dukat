type ComplexType<A, B, C = Array<A>> = A | B | C

declare class A {
    x: ComplexType<number, string>
    y: ComplexType<number, string, Array<string>>
}