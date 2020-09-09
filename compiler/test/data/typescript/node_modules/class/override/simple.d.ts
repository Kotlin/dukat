declare class Foo {
    bar();
    bar(a:number);
    baz: any
}

declare class Boo extends Foo {
    bar();
    bar(a:number);
    bar(a: string);
    baz: number
}
