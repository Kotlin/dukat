declare class Stream {}
declare class Writable extends Stream {}
declare class Readable extends Stream {}
declare class Duplex extends Readable implements Writable {}

interface ReadableOptions {
  read?(self: Readable, size: number): void;
}

interface WriteableOptions {
  write?(self: Writable, chunk: any, encoding: string, callback: (error?: Error | null) => void): void;
}

interface DuplexOptions extends ReadableOptions, WriteableOptions {
  read?(self: Duplex, size: number): void;
  write?(self: Duplex, chunk: any, encoding: string, callback: (error?: Error | null) => void): void;
}