declare  interface TLSSocket {}

declare class Server {
  once(event: "secureConnection", listener: (tlsSocket: TLSSocket) => void): this;
}

declare class HttpServer2  extends Server {
  once(event: "unknownProtocol", listener: (socket: TLSSocket) => void): this;
}

