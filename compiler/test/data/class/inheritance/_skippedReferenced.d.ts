declare interface Shape {}

declare interface Box extends Shape {}

declare interface BaseEvent {
    data: string | number;
    getDelegateTarget(): Shape;
    getElement(): Element;
    transform<T extends Shape>(shape?: T): T;
    prop: any;
}
