interface BaseSyntheticEvent<T = any> {
  target: T;
  ping(): T
}

interface ApiElement {}
interface EventTarget { }

interface SyntheticEvent extends BaseSyntheticEvent<EventTarget> {}

interface ApiFocusEvent<T = ApiElement> extends SyntheticEvent {
  target: EventTarget & T;
  ping(): number;
}