/// <reference path="_env.d.ts" />

type Comparator<T> = (a: T, b: T) => boolean;

interface Api<T> {
    compare(comparator: Comparator<T>)
}

interface Event {
    foo()
    bar
    [prop: string]: number;
    someField: string;
    optionalField?: any;
    ping(marker?: String)
    (resourceId:string, hash?:any, callback?:Function): void;
    (resourceId:number): boolean;
    ping(param: () => boolean): boolean;
}
