export declare class SomeClass {
    ping<T extends {
        target?: any
    }>(source: T): void;
    static foo<T extends {
        target?: any;
    }>(array: T[], classes?: any[]): T[];
}

export declare class SomeOtherClass<T extends {
    target: any
}> {
    ping(obj: T): boolean;
}

export declare interface OtherClassLikeInterface<T extends {
    target: any
}> {
    ping(obj: T): SomeOtherClass<T>;
}

export function transform<T extends {onTransformEnd: () => void}>(a: T): T;