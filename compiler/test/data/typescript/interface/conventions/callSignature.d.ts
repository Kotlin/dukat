interface Foo {
    ();
    (n: number): boolean;
    (foo: Foo, s: string): Bar;
    (a?: String, b?: Number, c?: Boolean)
}

interface Bar {

}