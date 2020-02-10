declare interface BaseEvent {
  getElement(): Element;
}

declare class BoxStringEvent implements BaseEvent {
  getElement(): HTMLElement;
}