declare namespace React {
    class Component<P, S> extends Bar implements Foo {
        boo(p: P, s: S)
    }
    interface Component<P, S> extends ComponentLifecycle<P, S> {
        foo: string
        bar(): number
        baz(a: any)
    }
    interface Foo {

    }
    class Bar {

    }
    interface ComponentLifecycle<T, U> {

    }
}
