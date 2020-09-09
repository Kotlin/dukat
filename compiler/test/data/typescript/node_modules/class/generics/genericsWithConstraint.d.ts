declare class Foo<T extends Bar> {
    varT: T;
    withoutArgumentsReturnsT(): T;
    withOneT(a: T): T;
    returnsB<B extends Baz>(a: any): B;
    withManyArguments<A extends T, B extends A>(a: A, b: B): T;
}

interface Bar {

}

interface Baz {

}