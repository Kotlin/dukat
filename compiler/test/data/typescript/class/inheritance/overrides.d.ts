declare interface Shape {}
declare interface InvariantBox<T> {}

declare interface Box extends Shape {}

declare interface BaseEvent {
    data: string | number;
    getDelegateTarget(): Shape;
    getElement(): Element;
    transform<T extends Shape>(shape?: T): T;
    prop: any;
    queryByReturnType(query: string, parameters?: any[]): InvariantBox<any>;
}
declare class BoxStringEvent implements BaseEvent {
    data: string;
    getDelegateTarget(): Box;
    getElement(): HTMLElement;
    transform<T extends Shape>(shape?: T): T;
    prop: string;
    queryByReturnType(query: string, parameters?: any[]): InvariantBox<string>;
}
declare interface NumberEvent extends BaseEvent {
    data: number;
    otherProp: any;
}

export class ParentClass {
    public prop: any;
}

export class ChildClass extends ParentClass {
    prop: string;
}