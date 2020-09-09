interface Foo<T> {
    methodWithOutArgs?();
    methodWithString?<A>(s: A): T;
    methodWithManyArgs?<A extends T, B>(n: A, settings: Bar): B;
}

interface Bar {

}