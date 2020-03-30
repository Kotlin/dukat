
interface Stream {}
interface Readable extends Stream {}
interface Duplex extends Readable {}

interface ReadableOptions {
  read?(self: Readable, size: number): void;
}

interface DuplexOptions extends ReadableOptions {
  read?(self: Duplex, size: number): void;
}