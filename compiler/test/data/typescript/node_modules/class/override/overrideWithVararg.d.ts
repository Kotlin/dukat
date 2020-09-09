
declare module "worker" {
  class EventEmitter {
    emit(event: string | symbol, ...args: any[]): boolean;
  }
  class Worker extends EventEmitter {
    emit(event: "message", value: any): boolean;
    emit(event: string | symbol, ...args: any[]): boolean;
  }
}

