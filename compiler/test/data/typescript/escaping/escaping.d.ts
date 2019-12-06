declare var val: any;
// declare var var: boolean;
declare var $foo: boolean;
declare function bar$(ba$z: number);
declare function fun();
interface This {
    "string-literal": boolean;
    'another-string-literal': string;
    "this_one_shouldnt_be_escaped": boolean;
    ":authority"?: string;
    ".xxx": any;
    '3х': string;
    200: string;
    300?: number;
    when: string;
    typealias: number;
    typeof: number;
    this: This;
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

    static class(): This
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
    interface interface {

    }
    interface $foo {

    }
}

declare module bar {
    module string {
        interface interface {

        }
    }
}

declare module $boo {
    interface typealias {

    }
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

type WatchHandler<T> = (val: T, oldVal: T) => void;

declare class $tring {

}

declare class Foo {

}

declare namespace fun {
    interface Promise<T> {

    }
}