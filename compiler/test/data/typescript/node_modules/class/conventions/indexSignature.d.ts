declare var foo: {[someKey: string]: number};

declare class Foo {
    [n: number]: Bar;
    [s: string]: string | string[];
    props?: {
        [someName: string]: { [someValue: string]: number };
    };
}
declare class Bar {

}
