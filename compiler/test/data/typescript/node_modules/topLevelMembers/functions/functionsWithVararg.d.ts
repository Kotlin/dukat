declare function withVarargWithoutTypeAnnotation(...a);
declare function withVarargAny(...a: any[]): any;
declare function withVarargNumber(...s: number[]): string;
declare function withManyArguments(n: number, ...s: string[]): boolean;
declare function withVarargWithGenericArrayOfNumber(...numbers: Array<number>): string;
declare function withVarargWithGenericArrayOfFoo(...foos: Array<Foo>): string;
declare interface Foo {

}