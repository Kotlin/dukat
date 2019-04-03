declare interface Shape {}

declare interface Box extends Shape {}

declare interface BaseEvent {
    data: string | number;
    getDelegateTarget(): Shape;
    getElement(): Element;
    transform<T extends Shape>(shape?: T): T;
}
declare class BoxStringEvent implements BaseEvent {
    data: string;
    getDelegateTarget(): Box;
    getElement(): HTMLElement;
    transform<T extends Shape>(shape?: T): T;
}
declare interface NumberEvent extends BaseEvent {
    data: number;
}
