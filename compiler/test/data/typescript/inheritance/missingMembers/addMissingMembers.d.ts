interface OptionsA {}

interface OptionsB extends OptionsA {}


interface MyWritableStream {
  ping(id: any);
  setOptions(options: OptionsA);
  write(str: string, encoding?: string, cb?: (err?: Error | null) => void): boolean;
}

declare class MyStreamWritable implements MyWritableStream {
  ping(id: string);
  setOptions(options: OptionsB);
  write(chunk: any, encoding: string, cb?: (error: Error | null | undefined) => void): boolean;
}

declare abstract class MyAbstractWritableStream {
  ping(id: any);
}

declare abstract class MyAbstractStreamWritable extends MyAbstractWritableStream {

}