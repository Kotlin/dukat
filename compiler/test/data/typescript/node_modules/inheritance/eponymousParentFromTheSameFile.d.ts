interface Something {
  ping(): boolean;
  pong(): boolean;
}

declare namespace Somenamespace {
  class Something implements Something {
    constructor(descriptor: string);
  }
}