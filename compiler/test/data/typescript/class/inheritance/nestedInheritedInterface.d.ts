export declare class Ping implements Ping.Pong {
  ping();
  pong();
}

declare function ping(p: Ping.Pong)

declare namespace Ping {
  interface Pong {
    pong();
  }
}

declare class Bing {
  ping(p: Ping.Pong)
}