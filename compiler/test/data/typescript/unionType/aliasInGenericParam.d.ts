export as namespace ourapp;

export type OurAlias =
    string | number | boolean | Date | OurObject | OurListArray;

export interface OurObject {
    readonly [x: string]: OurAlias;
}
export interface OurListArray extends Array<OurAlias> { }

