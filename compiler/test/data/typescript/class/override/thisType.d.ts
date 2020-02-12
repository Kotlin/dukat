

declare class Emitter {
  addEvent(name: String): this;
}

declare class ProcessEmitter extends Emitter {
  addEvent(name: String): this;
}

interface Array<T> {
  shuffle(): this;
}