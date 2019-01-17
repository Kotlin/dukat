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
}
