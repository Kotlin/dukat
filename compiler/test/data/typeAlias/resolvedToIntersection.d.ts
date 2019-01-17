type A<X, Y> = X & Y

declare class C {
}

declare function foo<T>(p: A<T, C>)