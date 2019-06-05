declare interface Shape {}

declare interface Box extends Shape {}

declare interface SortOfEventTarget {}
declare interface SortOfElement extends SortOfEventTarget {}

declare interface BaseEvent {
    data: string | number;
    getDelegateTarget(): Shape;
    getElement(): Element;
    transform<T extends Shape>(shape?: T): T;
    getSortOfEventTarget(): SortOfEventTarget;
    prop: any;
}
