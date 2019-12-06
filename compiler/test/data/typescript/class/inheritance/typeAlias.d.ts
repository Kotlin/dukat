
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

