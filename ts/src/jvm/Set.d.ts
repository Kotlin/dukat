declare class Set<T> {
  add(value: T): this;
  clear(): void;
  delete(value: T): boolean;
  has(value: T): void;
  forEach(callbackfn: (value: T, value2: T, set: this) => void, thisArg?: any): void;
}
