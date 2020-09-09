declare module a {
  interface SomeInterface<T> {}
  type AlphaNumeric = string | number;
}

declare var myInterface: a.SomeInterface<a.AlphaNumeric>;
export = myInterface;
