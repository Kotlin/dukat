interface AppEvent extends Event {
    currentTarget: Element | null;
    target: Element | undefined;
    preventDefault(): any;
}

interface LibEvent extends Event {
    preventDefault();
}

declare class NativeEvent extends Event {
    preventDefault(): any;
}

declare class FrameworkEvent extends Event {
    preventDefault();
}