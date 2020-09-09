declare var _: _.StaticInstance;
export = _;

declare module _ {
  type PropertyShorthand = string | number;

  interface Result {}
  interface ResultDerivation<T extends _.Result> {}

  interface StaticInstance {
    fetch(param: _.PropertyShorthand): _.Result[];
    ping<T extends _.ResultDerivation<T>>(): T;
  }
}