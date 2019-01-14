interface ComponentSpec<P,S> {}

interface Foo<T> {
    varT: T;
    withoutArgumentsReturnsT(): T;
    withOneT(a: T): T;
    returnsB<B>(a: any): B;
    withManyArguments<A, B>(a: A, b: B): T;
    withMultipleTypeParamsInParam<P, S>(spec: ComponentSpec<P, S>): String;
}
