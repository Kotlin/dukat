/// <reference path="../../../testDefinitelyTyped/DefinitelyTyped/jquery/jquery.d.ts" />

interface Event {
    foo()
    bar
    [prop: string]: number;
    someField: string;
    optionalField?: any;
    ping(marker?: String)
    (resourceId:string, hash?:any, callback?:Function): void;
}
