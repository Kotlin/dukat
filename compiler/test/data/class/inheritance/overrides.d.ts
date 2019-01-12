declare interface Shape {}

declare interface Box extends Shape {}

declare interface BaseEvent {
    data: string | number;
    getDelegateTarget(): Shape;
    getElement(): Element;
}
declare class BoxStringEvent implements BaseEvent {
    data: string;
    getDelegateTarget(): Box;
    getElement(): HTMLElement;
}
declare interface NumberEvent extends BaseEvent {
    data: number;
}
