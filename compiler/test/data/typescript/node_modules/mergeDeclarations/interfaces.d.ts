// based on lodash.d.ts
declare module _ {
    interface LoDashStatic {
        /**
         * Creates a lodash object that wraps the given value with explicit method chaining enabled.
         * @param value The value to wrap.
         * @return The wrapper object.
         **/
        chain(value: number): LoDashWrapper<number>;
        chain(value: string): LoDashWrapper<string>;
        chain(value: boolean): LoDashWrapper<boolean>;
        chain<T>(value: Array<T>): LoDashArrayWrapper<T>;
        chain(value: any): LoDashWrapper<any>;
    }


    //_.compact
    interface LoDashStatic {
        /**
         * Returns a copy of the array with all falsy values removed. In JavaScript, false, null, 0, "",
         * undefined and NaN are all falsy.
         * @param array Array to compact.
         * @return (Array) Returns a new array of filtered values.
         **/
        compact<T>(array: Array<T>): T[];

        /**
         * @see _.compact
         **/
        compact<T>(array: List<T>): T[];
    }

    interface LoDashWrapper<T> {

    }

    interface LoDashArrayWrapper<T> {

    }
}

declare interface SomeNode {}
declare interface Assignable {}

declare interface SomeElement extends SomeNode, Assignable {
    ping(): boolean;
}

declare interface SomeElement extends Assignable, SomeNode {
    pong(): boolean;
}

declare interface SomeElement extends SomeNode {
    bing(): boolean;
}

declare interface SomeElement extends Assignable {
    bong(): boolean;
}