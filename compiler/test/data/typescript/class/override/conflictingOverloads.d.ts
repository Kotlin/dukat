type StandardEvents = "cancel" | "submit" | "start"

declare class Message {
  once(event: 'abort', listener: () => void): this;
  once(event: 'timeout', listener: () => void): this;
  once(event: any, listener: () => void): this;
  once(event: 'close', listener: () => void): this;
  once(event: 'drain', listener: () => void): this;
  once(event: StandardEvents, listener: () => void): this;
  once(event: 'end' | 'finish' | 'basta', listener: () => void): this;
}

declare interface Ping {
  ping(a: Number | String);
  ping(a: Boolean | Number);
}

declare function addListener(event: "disconnect", listener: (worker: Ping) => void): Message;
declare function addListener(event: "online", listener: (worker: Ping) => void): Message;

