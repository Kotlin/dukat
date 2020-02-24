declare interface Chain<T, V = T> {
  shuffle(): Chain<T>
  shuffleString(): Chain<string>
  shuffleLambda(): Chain<() => string | undefined>
}

declare interface ChainOfArrays<G> extends Chain<G[]> {
  flatten(shallow?: boolean): Chain<G>;
}

declare interface AsyncResultObjectCallback<T, E = Error> { (err: E | undefined, results: Array<T | undefined>): void; }

declare function transform<T, R, E = Error>(
  arr: {[key: string]: T},
  iteratee: (acc: {[key: string]: R}, item: T, key: string, callback: (error?: E) => void) => void,
  callback?: AsyncResultObjectCallback<T, E>
): void