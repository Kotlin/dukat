/// <reference path="../../testDefinitelyTyped/DefinitelyTyped/jquery/jquery.d.ts" />

interface Event {
    foo()
    bar
    [prop: string]: number;
    someField: string;
    optionalField?: any;
    (resourceId:string, hash?:any, callback?:Function): void;
}
