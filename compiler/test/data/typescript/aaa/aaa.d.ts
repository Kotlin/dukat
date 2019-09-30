declare interface Chain<T, V = T> {
  shuffle(): Chain<T>
  shuffleString(): Chain<string>
  shuffleLambda(): Chain<() => string | undefined>
}

declare interface ChainOfArrays<T> extends Chain<T[]> {
  flatten(shallow?: boolean): Chain<T>;
}