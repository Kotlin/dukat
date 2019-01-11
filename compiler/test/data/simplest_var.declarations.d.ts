declare function withoutArgumentsReturnsT<T>(): T;
declare function withOneT<T>(a: T): any;
declare function returnsT<T>(s: string): T;
declare function withManyArguments<A, B>(a: A, b: B): boolean;

declare var arrayOfAny: Array<any>;
declare var arrayOfArray: Array<Array<string>>;
declare var arrayOfList: Array<List<string>>;
declare var arrayOfListBySquare: List<boolean>[];
declare var listOfArray: List<Array<any>>;
declare var listOfArrayBySquare: List<number[]>;

declare function withVarargWithoutTypeAnnotation(...a);
declare function withVarargAny(...a: any[]): any;
declare function withVarargNumber(...s: number[]): string;
declare function withManyArguments(n: number, ...s: string[]): boolean;
declare function withVarargWithGenericArrayOfNumber(...numbers: Array<number>): string;
declare function withVarargWithGenericArrayOfFoo(...foos: Array<Foo>): string;

declare function foo(): void;
declare function foo2(a: number): void;
declare function foo3(b: number): string;
declare function foo3(c: number, d: string): string;

declare function foofun(a: () => void): void;
declare function foofun2(a: (n: number) => void): void;

declare function foofun3(f?: (a) => boolean): boolean;

declare class Foo {
  constructor();
}
//
// declare class Bar {
//   constructor(a: number);
// }

declare var a: string;
declare var b: number;
declare var c: boolean;
declare var u: undefined;
declare var n: null;

declare var a0: string | null;
declare var b0: number | null;
declare var c0: boolean | null;


declare var aa: null | string;
declare var bb: null | number;
declare var cc: null | boolean;

declare var a3: string | number | null;
declare var c3: number |boolean | null;


declare var au: string | undefined;
declare var bu: number | undefined;
declare var cu: boolean | undefined;


declare var d: string[];
declare var e: number[];
declare var f: boolean[];

declare var d1: Array<string>;
declare var e1: Array<number>;
declare var f1: Array<boolean>;


declare var g: Number;
declare var h: String;


declare const ax: (string | null) | number
declare const bx: (string | null) | (number | undefined)

declare function withOneAny(a: any = 0): any;
declare function withOneString(s: string = "foobar"): string;
declare function withOneStringAndOptional(s: string = "something", settings?: JQueryAjaxSettings): boolean;
declare function withOneStringAndOptional(s: boolean = true): boolean;
declare function withOneStringAndOptional(s: boolean = false): boolean;