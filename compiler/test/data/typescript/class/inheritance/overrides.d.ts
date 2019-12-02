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
    thisIsNullable: String | null;
}
declare class BoxStringEvent implements BaseEvent {
    data: string;
    thisIsNullable: String;
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

declare interface _Chain<T, V> {
    flatten(shallow?: boolean): _Chain<any, any>
}

declare interface _ChainOfArrays<T> extends _Chain<Array<T>, Array<T>> {
    flatten(shallow?: boolean): _Chain<T, T>
}