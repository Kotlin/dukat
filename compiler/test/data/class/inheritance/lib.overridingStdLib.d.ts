interface AppEvent extends Event {
    currentTarget: Element;
    target: Element;
    preventDefault(): any;
}

declare class NativeEvent extends Event {
    preventDefault(): any;
}