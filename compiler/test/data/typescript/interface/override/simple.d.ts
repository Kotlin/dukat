interface Foo {
    bar();
    bar(a:number);
    baz: any
}

interface Boo extends Foo {
    bar();
    bar(b:number);
    bar(c: string);
    baz: number
}
