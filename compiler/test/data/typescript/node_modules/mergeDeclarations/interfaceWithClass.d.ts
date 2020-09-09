declare namespace React {
    interface Component<P, S> extends ComponentLifecycle<P, S> {
        foo: string
        bar(): number
        baz(a: any)
    }
    class Component<P, S, T = boolean> extends Bar implements Foo {
        boo(p: P, s: S)
    }
    interface Foo {

    }
    class Bar {

    }
    interface ComponentLifecycle<T, U> {

    }
}
