
declare function frequencies(a: ReadonlyArray<String>): ReadonlyArray<number>

interface Processor<T> {
  process(arg: T | ReadonlyArray<T>)
  alias(aliases: { [shortName: string]: string | ReadonlyArray<string> });
}