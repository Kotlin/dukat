import * as ping from "ping";

declare function withOneAny(a?: any): any;
declare function withOneString(s?: string): string;
declare function withManyArguments(s?: string, settings?: JQueryAjaxSettings): boolean;

declare function withOptionalQualified(opts?: ping.Options): void;

declare interface JQueryAjaxSettings {

}