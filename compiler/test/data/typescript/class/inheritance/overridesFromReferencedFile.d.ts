/// <reference path="_skippedReferenced.d.ts" />
declare class BoxStringEvent implements BaseEvent {
    data: string;
    getDelegateTarget(): Box;
    getElement(): HTMLElement;
    transform<T extends Shape>(shape?: T): T;
    getSortOfEventTarget(): SortOfElement;
    prop: string;
}
declare interface NumberEvent extends BaseEvent {
    data: number;
    otherProp: any;
}