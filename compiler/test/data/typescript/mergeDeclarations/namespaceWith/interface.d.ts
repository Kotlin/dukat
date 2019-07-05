declare namespace MyFramework {
  interface MyEvent {
    ping(): void;
  }
  class MyClass {}
  let prop: string;
}

declare interface MyFramework {
  triggerHandler(eventType_event: MyFramework.MyEvent): any;
}