interface AppEvent extends Event {
    preventDefault(): any;
}

declare class NativeEvent extends Event {
    preventDefault(): any;
}