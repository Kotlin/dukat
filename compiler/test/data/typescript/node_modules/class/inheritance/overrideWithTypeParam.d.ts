declare interface Writable {}
declare interface Readable {}

declare interface ChildProcess {
  stdin: Writable;
  ping(): Readable;
}

declare interface ChildProcessGeneralized<I extends Writable, K extends Readable> extends ChildProcess{
  stdin: I;
  ping(): K;
}

interface SomeBase {}
declare class SomeImplementation implements SomeBase{ }

interface Base<T, S extends SomeBase> {
  ping(o: T);
  pong(): T;
  bang(s: S);
}

declare class App implements Base<number, SomeImplementation> {
  ping(o: number);
  pong(): number;
  bang(s: SomeImplementation);
}