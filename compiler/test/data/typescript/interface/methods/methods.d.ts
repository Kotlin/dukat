interface Foo {
    new(n: number): foo.Bar
    methodWithOutArgs();
    methodWithString(s: string): string;
    methodWithManyArgs(n: number, settings: foo.Bar): boolean;
}
declare namespace foo {
    interface Bar {
        methodWithString(s: string): string;
    }
}
