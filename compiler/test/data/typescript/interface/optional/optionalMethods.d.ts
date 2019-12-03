interface Foo {
    methodWithOutArgs?();
    methodWithString?(s: string): string;
    methodWithManyArgs?(n: number, settings: Bar): boolean;
}

interface Bar {

}