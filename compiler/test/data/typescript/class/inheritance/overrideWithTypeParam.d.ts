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