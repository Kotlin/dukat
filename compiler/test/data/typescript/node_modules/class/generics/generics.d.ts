declare class Foo<T> {
    varT: T;
    withoutArgumentsReturnsT(): T;
    withOneT(a: T): T;
    returnsB<B>(a: any): B;
    withManyArguments<A, B>(a: A, b: B): T;
}
