declare var val: any;
// declare var var: boolean;
declare var $foo: boolean;
declare function bar$(ba$z: number);
declare function fun();
interface This {
    "string-literal": boolean,
    'another-string-literal': string,
    when: string;
    typealias: number;
    typeof: number;
    in(object: Foo);
    is(value: any): Boolean;
    return(): any;
    return(this: This): Number;
    throw(reason: Error);
    try(fn: () => any);
}
declare class is<interface> {
    as: number;
    package(a): boolean;

    static in(object: Foo);
    static is(value: any): Boolean;
    static return(): any;
    static return(this: This): Number;
    static throw(reason: Error);
    static try(fn: () => any);
}

declare module "This" {
    export var $foo: boolean;
    export function bar$(ba$z: number);
    var aaa: when.interface;
    var bbb: when.$foo;
}

declare module when {
    export var $: boolean;
    export function package(as: bar.string.interface, b: $boo.typealias): $tring;
}

declare function When<T, U>(value: when.Promise<T>, transform: (val: T) => U): fun.Promise<U>;

declare namespace when {
    interface Promise<T> {}
}

declare var _: __.___;

declare namespace __ {
    namespace xxx {
        interface Amsterdam {}
    }

    interface ___ {
    }

    interface _OK_ {
    }
}
