interface Foo {
    bar();
    bar(a:number);
    baz: any
}

interface Boo extends Foo {
    bar();
    bar(a:number);
    bar(a: string);
    baz: number
}
