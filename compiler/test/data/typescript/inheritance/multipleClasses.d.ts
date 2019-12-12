

declare class Ping {
    ping(): String;
}

declare class Pong {
  pong(): String;
}

declare class PingPong extends Ping implements Pong {
  pong(): String;
}