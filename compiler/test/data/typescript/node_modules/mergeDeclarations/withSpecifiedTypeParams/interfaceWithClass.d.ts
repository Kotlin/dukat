interface BaseA<T> {
  ping(o: T);
  pong(): T;
  uid: T;
}

interface BaseB<T> {
  description: T
}

interface Stats  extends BaseA<number>, BaseB<string> {}
declare class Stats {}

