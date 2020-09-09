
type Numba = Number;
type NumArray = Array<Numba>

interface Processor {
   process(input: NumArray): Array<Number>
}

type AliasedProcessor = Processor

declare class MultithreadedProcessor implements AliasedProcessor {
  process(input: Array<Number>): Array<Numba>;
}

declare class SinglethreadedProcessor implements AliasedProcessor {
  process(input: Array<Number>): NumArray;
}

type NumCollection<T extends number> = Array<T>

type Many<T> = T | ReadonlyArray<T>;
type PropertyName = string | number | symbol;
type PropertyPath = Many<PropertyName>;

interface SomethingStatic {
  pick<T extends object, U extends keyof T>(
    object: T,
    ...props: Array<PropertyPath>
  ): Pick<T, U>;
}