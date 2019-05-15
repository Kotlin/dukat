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
}
declare class is<interface> {
    as: number;
    package(a): boolean;
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
    interface ___ {
    }

    interface _OK_ {
    }
}
