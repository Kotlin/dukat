declare function withOneAny(a: any = 0): any;
declare function withOneString(s: string = "foobar"): string;
declare function withOneStringAndOptional(s: string = "something", settings?: JQueryAjaxSettings): boolean;
declare interface JQueryAjaxSettings {

}