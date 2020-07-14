declare class MyBuffer implements Uint8Array {
  every(callbackfn: (value: number, index: number, array: Uint8Array) => unknown, thisArg?: any): boolean;
}