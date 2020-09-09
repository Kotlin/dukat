interface BasicEmitter {
  emit(event: string | symbol, ...args: any[]): boolean;
}

declare interface internal extends BasicEmitter{ }

declare namespace internal {
  class EventEmitter extends internal {
    emit(event: string | symbol, ...args: any[]): boolean;
  }

  interface Computable {
    compute(): number
  }
}

declare interface ChildProcess extends internal.EventEmitter {
  emit(event: string | symbol, ...args: any[]): boolean;
}

declare class ComputableEntity implements internal.Computable {
  compute(): number;
}