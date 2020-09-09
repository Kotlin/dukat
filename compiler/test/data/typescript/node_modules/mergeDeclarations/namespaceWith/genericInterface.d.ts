declare namespace MyFramework {
  interface MyEvent {
    ping(): void;
  }
  //TODO: this is valid but I guess it would be nicer to rename generic param
  class MyClass<T extends Number> { ping(): T }
  let prop: string;
}

declare interface MyFramework<S, T> {
  triggerHandler(source: S, eventType_event: MyFramework.MyEvent): T;
}