
declare function frequencies(a: ReadonlyArray<String>): ReadonlyArray<number>

interface NumArray extends Array<Number> {}
interface SmartArray<T> extends Array<T> {}

declare class MyVerySpecificException extends Error {
  constructor(message?: string)
}

interface Processor<T> {
  process(arg: T | ReadonlyArray<T>)
  alias(aliases: { [shortName: string]: string | ReadonlyArray<string> });
  shape(input: SmartArray<String>): NumArray
  convertToSmartArray(input: NumArray): SmartArray<T>
  onError(handler: (error: MyVerySpecificException) => void);
}