export interface FooInterface {
    methodWithOutArgs();
    methodWithString(s: string): string;
    methodWithManyArgs(n: number, settings: Bar): boolean;
}

export class FooClass {
    methodWithOutArgs();
    methodWithString(s: string): string;
    methodWithManyArgs(n: number, settings: Bar): boolean;
}

export class Bar {

}