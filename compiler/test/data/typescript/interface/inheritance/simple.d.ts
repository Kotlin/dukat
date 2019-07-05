interface BaseJQueryEventObject extends Event {
    data: any;
    delegateTarget: Element;
    isDefaultPrevented(): boolean;
    isImmediatePropogationStopped(): boolean;
    isPropagationStopped(): boolean;
}
