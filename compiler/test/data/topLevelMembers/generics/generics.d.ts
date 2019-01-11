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
