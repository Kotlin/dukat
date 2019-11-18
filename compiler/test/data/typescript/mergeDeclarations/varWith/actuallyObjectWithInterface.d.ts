
interface ArrayType<T> {
  type: T;
  new(length?: number): { [i: number]: T; length: number; toArray(): T[]; }
}

declare  var ArrayType: {
  BYTES: number
};