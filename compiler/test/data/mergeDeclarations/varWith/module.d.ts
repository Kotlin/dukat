// based on lodash.d.ts
declare var _: _.LoDashStatic;

declare module _ {
	interface LoDashStatic {
        (value: number): LoDashArrayWrapper<number>;
        VERSION: string;
        support: Support;
    }

    interface Support {
        argsClass: boolean;
        argsObject: boolean;
    }

    interface LoDashArrayWrapper<T> {
        difference(...others: Array<T>[]): LoDashArrayWrapper<T>;
        difference(...others: List<T>[]): LoDashArrayWrapper<T>;
    }
}

declare module "lodash" {
	export = _;
}
